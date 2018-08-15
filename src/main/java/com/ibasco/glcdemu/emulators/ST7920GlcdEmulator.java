package com.ibasco.glcdemu.emulators;

import com.ibasco.glcdemu.*;
import com.ibasco.glcdemu.instructions.DdramSet;
import com.ibasco.glcdemu.decoders.GlcdSpiCommDecoder;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * ST7920 GLCD Emulator
 *
 * @author Rafael Ibasco
 */
@SuppressWarnings("Duplicates")
public class ST7920GlcdEmulator {
    private static final Logger log = getLogger(ST7920GlcdEmulator.class);

    private GlcdCommDecoder protocolDecoder;

    private InstructionListener instructionListener;

    private GpioActivityListener gpioActivityListener;

    private DataListener dataListener;

    private int registerSelect = 0;

    private int[] registerData = new int[2];

    private int registerCounter = 0;

    private static final int RS_INSTRUCTION = 0xF8;

    private static final int RS_DATA = 0xFA;

    private short[][] buffer = new short[32][16];

    private int yAddress = 0;

    private int xAddress = 0;

    private int dataCtr = 0;

    @FunctionalInterface
    public interface GpioActivityListener {
        void onGpioActivity(int pin, int value);
    }

    @FunctionalInterface
    public interface InstructionListener {
        void onInstructionEvent(GlcdInstruction instruction);
    }

    @FunctionalInterface
    public interface DataListener {
        void onDataEvent(short[][] buffer);
    }

    /**
     * Pass the gpio event source
     */
    public ST7920GlcdEmulator(GpioEventDispatcher dispatcher) {
        dispatcher.addListener(this::processGpioEvents);
        dispatcher.initialize();
        this.protocolDecoder = new GlcdSpiCommDecoder(this::handleByteEvent);
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public void setInstructionListener(InstructionListener instructionListener) {
        this.instructionListener = instructionListener;
    }

    public void setGpioActivityListener(GpioActivityListener gpioActivityListener) {
        this.gpioActivityListener = gpioActivityListener;
    }

    /**
     * @return The display buffer
     */
    public short[][] getBuffer() {
        return buffer;
    }

    /**
     * Clears the display buffer
     */
    public void clearBuffer() {
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 16; x++) {
                buffer[y][x] = 0x0;
            }
        }
    }

    /**
     * Process and decode incoming GPIO events
     *
     * @param gpioActivity
     *         Gpio Event
     */
    private void processGpioEvents(GpioActivity gpioActivity) {
        protocolDecoder.decode(gpioActivity);
        gpioActivityListener.onGpioActivity(gpioActivity.getMsg(), gpioActivity.getValue());
    }

    /**
     * This method is called when a byte is ready for processing
     *
     * @param b
     *         The byte to be processed
     */
    private void handleByteEvent(int b) {
        //Select register
        if (b == RS_INSTRUCTION || b == RS_DATA) {
            registerSelect = b;
        } else {
            //Store data to register
            registerData[registerCounter] = b;

            //re-assemble after having collected the two consecutive bytes (high & low)
            if (registerCounter == 1) {
                int val = registerData[0] | (registerData[1] >> 4);
                if (registerSelect == RS_INSTRUCTION) {
                    processInstruction(val);
                } else if (registerSelect == RS_DATA) {
                    processData(val);
                } else {
                    log.warn("Invalid register select value = {}", registerSelect);
                }
            }
            registerCounter = ++registerCounter & 0x1;
        }
    }

    private void processInstruction(int value) {
        GlcdInstruction instruction = GlcdInstructionFactory.createInstruction(value);

        if (instruction != null) {
            if (instructionListener != null)
                instructionListener.onInstructionEvent(instruction);

            switch (instruction.getFlag()) {
                case GlcdInstruction.F_CGRAM_SET:
                    break;
                case GlcdInstruction.F_DDRAM_SET:
                    DdramSet ins = (DdramSet) instruction;
                    if (ins.getAddressType() == DdramSet.ADDRESS_X) {
                        xAddress = ins.getAddress() & DdramSet.ADDRESS_X;
                    } else if (ins.getAddressType() == DdramSet.ADDRESS_Y) {
                        yAddress = ins.getAddress() & DdramSet.ADDRESS_Y;
                    }
                    break;
                case GlcdInstruction.F_DISPLAY_CLEAR:
                    log.info("DISPLAY CLEAR");
                    //not yet implemented
                    break;
                case GlcdInstruction.F_ENTRY_MODE_SET:
                    //not yet implemented
                    break;
                case GlcdInstruction.F_HOME:
                    //not yet implemented
                    break;
                case GlcdInstruction.F_DISPLAY_CURSOR_CONTROL:
                    //not yet implemented
                    break;
                case GlcdInstruction.F_FUNCTION_SET:
                    //not yet implemented
                    break;
                case GlcdInstruction.F_DISPLAY_CONTROL:
                    //not yet implemented
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
     *         The data in bytes
     */
    private void processData(int data) {
        //first byte
        if (dataCtr == 0) {
            buffer[yAddress][xAddress] = (short) ((data & 0xff) << 8);
        }
        //second byte
        else {
            buffer[yAddress][xAddress] |= data & 0xff;
            xAddress = ++xAddress & 0xf;

            //Notify listeners that the display buffer has been updated
            if (dataListener != null)
                dataListener.onDataEvent(buffer);
        }
        dataCtr = ++dataCtr & 0x1;
    }
}
