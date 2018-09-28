package com.ibasco.glcdemu.emulator.st7920;

import com.ibasco.glcdemu.emulator.st7920.instructions.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public class ST7920InstructionFactory {
    private static int addressCtr = 0;

    public static ST7920Instruction createInstruction(int value) {

        ST7920InstructionFlag flag = getInstructionFlag(value);

        if (flag == null)
            throw new RuntimeException("No instruction flags match for the specified value : " + Integer.toHexString(value).toUpperCase());

        ST7920Instruction instruction = null;

        switch (flag) {
            case DISPLAY_CONTROL:
                instruction = new DisplayControl((byte) value);
                break;
            case DISPLAY_CLEAR:
                instruction = new DisplayClear((byte) value);
                break;
            case HOME:
                instruction = new DisplayHome((byte) value);
                break;
            case ENTRY_MODE_SET:
                instruction = new EntryModeSet((byte) value);
                break;
            case DISPLAY_CURSOR_CONTROL:
                instruction = new DisplayCursorControl((byte) value);
                break;
            case FUNCTION_SET:
                instruction = new FunctionSet((byte) value);
                break;
            case CGRAM_SET:
                instruction = new CgramSet((byte) value);
                break;
            case DDRAM_SET:
                //Note: Max Y = 0x3F, Max X = 0xF
                int addressType = (addressCtr == 0) ? DdramSet.ADDRESS_Y : DdramSet.ADDRESS_X;
                instruction = new DdramSet((byte) value, addressType);
                addressCtr = (addressCtr + 1) & 0x1;
                break;
        }
        return instruction;
    }

    private static ST7920InstructionFlag getInstructionFlag(int instruction) {
        //match against a list of known instruction flags
        for (ST7920InstructionFlag flag : ST7920InstructionFlag.values()) {
            if (flag.matches(instruction))
                return flag;
        }
        return null;
    }
}
