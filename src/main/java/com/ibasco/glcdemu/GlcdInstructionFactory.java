package com.ibasco.glcdemu;

import com.ibasco.glcdemu.instructions.*;

import static com.ibasco.glcdemu.GlcdInstruction.*;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
public class GlcdInstructionFactory {
    private static int addressCtr = 0;

    public static GlcdInstruction createInstruction(int value) {
        int flag = getInstructionFlag(value);
        GlcdInstruction instruction = null;

        switch (flag) {
            case F_DISPLAY_CONTROL:
                instruction = new DisplayControl((byte) value);
                break;
            case F_DISPLAY_CLEAR:
                instruction = new DisplayClear((byte) value);
                break;
            case F_HOME:
                instruction = new DisplayHome((byte) value);
                break;
            case F_ENTRY_MODE_SET:
                instruction = new EntryModeSet((byte) value);
                break;
            case F_DISPLAY_CURSOR_CONTROL:
                instruction = new DisplayCursorControl((byte) value);
                break;
            case F_FUNCTION_SET:
                instruction = new FunctionSet((byte) value);
                break;
            case F_CGRAM_SET:
                instruction = new CgramSet((byte) value);
                break;
            case F_DDRAM_SET:
                //Note: Max Y = 0x3F, Max X = 0xF
                int addressType = (addressCtr == 0) ? DdramSet.ADDRESS_Y : DdramSet.ADDRESS_X;
                instruction = new DdramSet((byte) value, addressType);
                addressCtr = (addressCtr + 1) & 0x1;
                break;
        }
        return instruction;
    }

    private static int getBitPos(int flag) {
        int pos = 0;
        for (int i = 0x1; (i != flag) && (pos < 8); i <<= 1)
            pos++;
        return pos >= 8 ? -1 : pos;
    }

    private static boolean hasInstruction(int instruction, int flag) {
        int flagBitPos = getBitPos(flag);
        if (flagBitPos <= -1)
            return false;
        byte lhs = (byte) (instruction >> flagBitPos);
        byte rhs = (byte) (flag >> flagBitPos);
        return lhs == rhs;
    }

    private static int getInstructionFlag(int instruction) {
        if (hasInstruction(instruction, F_DISPLAY_CONTROL)) {
            return F_DISPLAY_CONTROL;
        } else if (hasInstruction(instruction, F_DISPLAY_CLEAR)) {
            return F_DISPLAY_CLEAR;
        } else if (hasInstruction(instruction, F_HOME)) {
            return F_HOME;
        } else if (hasInstruction(instruction, F_ENTRY_MODE_SET)) {
            return F_ENTRY_MODE_SET;
        } else if (hasInstruction(instruction, F_DISPLAY_CURSOR_CONTROL)) {
            return F_DISPLAY_CURSOR_CONTROL;
        } else if (hasInstruction(instruction, F_FUNCTION_SET)) {
            return F_FUNCTION_SET;
        } else if (hasInstruction(instruction, F_CGRAM_SET)) {
            return F_CGRAM_SET;
        } else if (hasInstruction(instruction, F_DDRAM_SET)) {
            return F_DDRAM_SET;
        }
        return -1;
    }
}
