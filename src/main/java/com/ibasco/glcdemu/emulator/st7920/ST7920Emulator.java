package com.ibasco.glcdemu.emulator.st7920;

import com.ibasco.glcdemu.emulator.GlcdEmulatorBase;
import com.ibasco.glcdemu.emulator.st7920.instructions.DdramSet;
import com.ibasco.pidisplay.drivers.glcd.Glcd;
import com.ibasco.pidisplay.drivers.glcd.GlcdDisplay;
import org.slf4j.Logger;

import java.nio.BufferOverflowException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * ST7920 GLCD Emulator
 *
 * @author Rafael Ibasco
 */
@SuppressWarnings("Duplicates")
public class ST7920Emulator extends GlcdEmulatorBase {
    private static final Logger log = getLogger(ST7920Emulator.class);

    //<editor-fold desc="Properties">
    private int registerSelect = 0;

    private int[] registerData = new int[2];

    private int registerCounter = 0;

    private short _data = 0;

    private static final int RS_INSTRUCTION = 0xF8;

    private static final int RS_DATA = 0xFA;

    private int yAddress = 0;

    private int xAddress = 0;

    private final AtomicInteger dataCtr = new AtomicInteger(0);
    //</editor-fold>

    @Override
    public void processByte(int data) {
        //Select register
        if (data == RS_INSTRUCTION || data == RS_DATA) {
            registerSelect = data;
        } else {
            //Store data to register
            registerData[registerCounter] = data;

            //re-assemble after having receiving two consecutive bytes (high & low)
            if (registerCounter == 1) {
                //combine high and low nibble byte
                int val = registerData[0] | (registerData[1] >> 4);
                if (registerSelect == RS_INSTRUCTION) {
                    processInstruction(val);
                } else if (registerSelect == RS_DATA) {
                    processData(val);
                } else {
                    log.warn("Unrecognized register select value = {}", registerSelect);
                }
            }
            //make sure to limit the counter range between 0 and 1 only (0 = high byte, 1 = low byte)
            registerCounter = ++registerCounter & 0x1;
        }
    }

    @Override
    public GlcdDisplay[] getSupportedDisplays() {
        return new GlcdDisplay[] {Glcd.ST7920.D_128x64, Glcd.ST7920.D_192x32};
    }

    /**
     * Process incoming data and write to display buffer. Per the ST7920 datasheet, the x-address will automatically be
     * incremented after receiving the second byte. Once the x-address reaches it's max range (0xf) the value will reset
     * back to 0x0.
     *
     * @param data
     *         An integer representing a byte of data
     */
    private void processData(int data) {
        //Note: For one address, two succeeding bytes are received (16 bits)

        //first byte (high nibble)
        if (dataCtr.getAndUpdate(this::boolIncrement) == 0) { //dataCtr == 0
            _data = (short) ((data & 0xff) << 8);
        }
        //second byte (low nibble)
        else {
            _data |= data & 0xff;
            try {
                //Process 2 bytes of data at a time then iterate through each bit starting
                // from the most significant bit. Flush to pixel buffer
                flush(_data);
            } finally {
                _data = 0;
            }
            //increment x-address after receving the second byte
            xAddress = ++xAddress & 0xf;
        }
    }

    /**
     * This will process 16-bit of data and flushes the processed data to the pixel buffer
     *
     * @param data
     *         A 16-bit value to be flushed to the display buffer
     */
    private void flush(short data) {
        int width = getBuffer().getWidth();
        int mask = width - 1;
        int offset = getBuffer().getHeight() / 2;

        for (int pos = 15; pos >= 0; pos--) {
            int x = (15 - pos) + (xAddress * 16); //calculate x-pixel coordinate
            int y = yAddress; //y-pixel coordinate (as is)
            boolean value = (data & (1 << pos)) != 0; //read nth bit

            //re-adjust x and y coordinates if overflow occurs
            if (x >= width) {
                x &= mask; //apply mask to limit range between 0 and (width -1)
                y += offset; //increment y with the calculated offset
                if (y > (getBuffer().getHeight() - 1))
                    throw new BufferOverflowException();
            }

            //Write to pixel buffer
            getBuffer().write(x, y, value);
        }
    }

    /**
     * Process incoming display instructions
     *
     * @param value
     *         The instruction data
     */
    private void processInstruction(int value) {
        ST7920Instruction instruction = ST7920InstructionFactory.createInstruction(value);

        if (instruction != null) {
            //log.debug("Processing Instruction: {}", instruction.getName());
            switch (instruction.getFlag()) {
                case CGRAM_SET:
                    break;
                case DDRAM_SET:
                    DdramSet ins = (DdramSet) instruction;
                    if (ins.getAddressType() == DdramSet.ADDRESS_X) {
                        xAddress = ins.getAddress() & DdramSet.ADDRESS_X;
                        //log.debug("DDRAM Set X = {}, Y = {}", xAddress, yAddress);
                    } else if (ins.getAddressType() == DdramSet.ADDRESS_Y) {
                        yAddress = ins.getAddress() & DdramSet.ADDRESS_Y;
                    }
                    break;
                case DISPLAY_CLEAR:
                    //not yet implemented
                    break;
                case ENTRY_MODE_SET:
                    //not yet implemented
                    break;
                case HOME:
                    //not yet implemented
                    break;
                case DISPLAY_CURSOR_CONTROL:
                    //not yet implemented
                    break;
                case FUNCTION_SET:
                    //not yet implemented
                    break;
                case DISPLAY_CONTROL:
                    //not yet implemented
                    break;
                default:
                    break;
            }
        }
    }

    private int boolIncrement(int prev) {
        return ++prev & 0x1;
    }
}
