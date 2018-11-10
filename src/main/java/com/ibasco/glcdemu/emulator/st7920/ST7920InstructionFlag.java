/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: ST7920InstructionFlag.java
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
package com.ibasco.glcdemu.emulator.st7920;

import com.ibasco.glcdemu.emulator.GlcdInstructionFlag;

import java.util.Arrays;

public enum ST7920InstructionFlag implements GlcdInstructionFlag {
    DISPLAY_CLEAR(0x1, "Fill DDRAM with \"20H\" and set DDRAM address counter (AC) to \"00H\""),
    HOME(0x2, "Set DDRAM address counter (AC) to \"00H\", and put cursor to origin"),
    ENTRY_MODE_SET(0x4, "Set cursor position and display shift when doing write or read operation"),
    DISPLAY_CONTROL(0x8, "Turns Display/Character blink ON or OFF"),
    DISPLAY_CURSOR_CONTROL(0x10, "Cursor position and display shift control"),
    FUNCTION_SET(0x20, "Switch between 4/8bit instruction set or "),
    CGRAM_SET(0x40, "Set CGRAM address to address counter"),
    DDRAM_SET(0x80, "Set DDRAM address to address counter (AC)");

    private int code;

    private String description;

    ST7920InstructionFlag(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ST7920InstructionFlag valueOf(int code) {
        return Arrays.stream(values()).filter(p -> p.code == code).findFirst().orElse(null);
    }

    @Override
    public int getCode() {
        return code;
    }
}
