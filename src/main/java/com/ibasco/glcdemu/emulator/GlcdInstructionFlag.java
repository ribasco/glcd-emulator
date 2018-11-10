/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdInstructionFlag.java
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
package com.ibasco.glcdemu.emulator;

/**
 * An interface representing an instruction of the display.
 *
 * @author Rafael Ibasco
 */
public interface GlcdInstructionFlag {

    /**
     * @return The unsigned byte representing the instruction flag
     */
    int getCode();

    /**
     * @return The description of the instruction flag
     */
    String getDescription();

    /**
     * Retrieve the {@link GlcdInstructionFlag} enum based on the instruction code provided.
     *
     * @param code
     *         The instruction code
     *
     * @return The {@link GlcdInstructionFlag} enum that matches the instruction code provided
     */
    GlcdInstructionFlag valueOf(int code);

    /**
     * Returns the bit position of the flag
     *
     * @param flag
     *         The display instruction flag
     *
     * @return The bit position 0 to 7. -1 if not found.
     */
    default int pos(int flag) {
        int pos = 0;
        for (int i = 0x1; (i != flag) && (pos < 8); i <<= 1)
            pos++;
        return pos >= 8 ? -1 : pos;
    }

    /**
     * Method to check if the value specified matches the current flag
     *
     * @param value
     *         An integer representing an instruction
     *
     * @return True if the value specified contains the matching flag
     */
    default boolean matches(int value) {
        int flagBitPos = pos(getCode());
        if (flagBitPos <= -1)
            return false;
        byte lhs = (byte) (value >> flagBitPos);
        byte rhs = (byte) (getCode() >> flagBitPos);
        return lhs == rhs;
    }
}
