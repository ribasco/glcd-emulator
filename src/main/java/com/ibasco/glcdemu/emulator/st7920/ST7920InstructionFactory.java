/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: ST7920InstructionFactory.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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
