/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: ST7920Emulator.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Luis Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
package com.ibasco.glcdemulator.emulator.st7920;

import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.emulator.GlcdEmulatorBase;
import com.ibasco.glcdemulator.emulator.st7920.instructions.DdramSet;
import com.ibasco.glcdemulator.exceptions.EmulatorProcessException;
import com.ibasco.glcdemulator.utils.ByteUtils;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.ucgdisplay.core.u8g2.U8g2Message;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBufferType;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ST7920 GLCD Emulator. Current implementation only supports spi/parallel interface (8-bit). Note that 4-bit parallel
 * mode is not supported by U8G2 so this feature is not implemented.
 *
 * @author Rafael Ibasco
 */
@Emulator(
        controller = GlcdControllerType.ST7920,
        description = "Emulator for ST7920 controller",
        bus = {
                GlcdBusInterface.SPI_HW_4WIRE_ST7920,
                GlcdBusInterface.SPI_SW_4WIRE_ST7920,
                GlcdBusInterface.PARALLEL_6800,
                GlcdBusInterface.PARALLEL_8080
        },
        defaultBus = GlcdBusInterface.PARALLEL_8080,
        bufferLayout = GlcdBufferType.HORIZONTAL
)
public class ST7920Emulator extends GlcdEmulatorBase {
    private static final Logger log = LoggerFactory.getLogger(ST7920Emulator.class);

    //<editor-fold desc="Constants">
    private static final int SER_RS_INSTRUCTION = 0xF8;

    private static final int SER_RS_DATA = 0xFA;
    //</editor-fold>

    //<editor-fold desc="Emulator Properties">
    /**
     * 0 = Command, 1 = Data
     */
    private int registerSelect = 0;

    private int[] registerData = new int[2];

    private int registerCounter = 0;

    private short _data = 0;

    private int yAddress = 0;

    private int xAddress = 0;

    private final AtomicInteger dataCtr = new AtomicInteger(0);
    //</editor-fold>

    @Override
    public final void onByteEvent(U8g2ByteEvent event) {
        U8g2Message msg = event.getMessage();
        switch (msg) {
            case U8X8_MSG_BYTE_SET_DC:
                registerSelect = event.getValue();
                break;
            case U8X8_MSG_BYTE_SEND: {
                int value = event.getValue();
                switch (getBusInterface()) {
                    case PARALLEL_8080:
                    case PARALLEL_6800: {
                        processParallel(value);
                        break;
                    }
                    case SPI_HW_4WIRE_ST7920:
                    case SPI_SW_4WIRE_ST7920: {
                        processSPI(value);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void processSPI(int data) {
        //Select register
        if (data == SER_RS_INSTRUCTION || data == SER_RS_DATA) {
            registerSelect = data;
        } else {
            //Store data to register
            registerData[registerCounter] = data;

            //re-assemble after having receiving two consecutive bytes (high & low)
            if (registerCounter == 1) {
                //combine high and low nibble byte
                int val = registerData[0] | (registerData[1] >> 4);
                if (registerSelect == SER_RS_INSTRUCTION) {
                    processInstruction(val);
                } else if (registerSelect == SER_RS_DATA) {
                    processData(val);
                } else {
                    throw new EmulatorProcessException(this, "Unrecognized register select value: " + ByteUtils.toHexString((byte) registerSelect));
                }
            }
            //make sure to limit the counter range between 0 and 1 only (0 = high byte, 1 = low byte)
            registerCounter = ++registerCounter & 0x1;
        }
    }

    private void processParallel(int data) {
        if (registerSelect == 0) {
            processInstruction(data);
        } else if (registerSelect == 1) {
            processData(data);
        }
    }

    /**
     * Process display instruction sets
     *
     * @param value
     *         The instruction data
     */
    private void processInstruction(int value) {
        ST7920Instruction instruction = ST7920InstructionFactory.createInstruction(value);

        if (instruction != null) {
            switch (instruction.getFlag()) {
                case CGRAM_SET:
                    break;
                case DDRAM_SET:
                    DdramSet ins = (DdramSet) instruction;
                    if (ins.getAddressType() == DdramSet.ADDRESS_X) {
                        xAddress = ins.getAddress() & DdramSet.ADDRESS_X;
                    } else if (ins.getAddressType() == DdramSet.ADDRESS_Y) {
                        yAddress = ins.getAddress() & DdramSet.ADDRESS_Y;
                    }
                    break;
                case DISPLAY_CLEAR:
                    //not implemented
                    break;
                case ENTRY_MODE_SET:
                    //not yet implemented
                    break;
                case HOME:
                    //not implemented
                    break;
                case DISPLAY_CURSOR_CONTROL:
                    //not implemented
                    break;
                case FUNCTION_SET:
                    //not implemented
                    break;
                case DISPLAY_CONTROL:
                    //not implemented
                    break;
                default:
                    break;
            }
        }
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
        if (dataCtr.getAndUpdate(this::toggle) == 0) { //dataCtr == 0
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
     * This will process 16-bit of data and flushes it to the pixel buffer
     *
     * @param data
     *         A 16-bit value to be flushed to the display buffer
     */
    private void flush(short data) {
        int width = getBuffer().getWidth();
        int mask = width - 1;
        int offset = getBuffer().getHeight() / 2; //this would be our overflow offset

        PixelBuffer buffer = getBuffer();

        for (int pos = 15; pos >= 0; pos--) {
            int x = (15 - pos) + (xAddress * 16); //calculate x-pixel coordinate
            int y = yAddress; //y-pixel coordinate (as is)
            boolean value = (data & (1 << pos)) != 0; //read nth bit

            //re-adjust x and y coordinates when overflow occurs
            if (x >= width) {
                x &= mask; //apply mask to limit range between 0 and (width -1)
                y += offset; //increment y with the overflow offset
                if (y > (buffer.getHeight() - 1)) {
                    throw new IllegalStateException(String.format("Y-coordinate greater than the maximum display height (actual: %d, max: %d)", y, getBuffer().getHeight() - 1));
                }
            }

            //Write to pixel buffer
            buffer.write(x, y, value);
        }
    }

    /**
     * Reset the buffer and properties
     */
    @Override
    public void reset() {
        dataCtr.set(0);
        registerSelect = 0;
        registerCounter = 0;
        registerData = new int[2];
        _data = 0;
        xAddress = 0;
        yAddress = 0;
    }

    private int toggle(int prev) {
        return ++prev & 0x1;
    }
}
