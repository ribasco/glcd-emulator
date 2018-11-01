/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: BitUtils.java
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
package com.ibasco.glcdemu.utils;

/**
 * Bit manipulation helper class
 *
 * @author Rafael Ibasco
 */
public class BitUtils {
    /**
     * Reads bit data based on the bit-position provided
     *
     * @param data
     *         The byte data to be queried
     * @param pos
     *         The bit position (0 to 7)
     *
     * @return A boolean integer 0=off, 1=on
     */
    public static int readBit(byte data, int pos) {
        pos &= 0x7; //limit values to 0 and 7 only
        return (data >> pos) & 0x1;
    }

    public static int readBit(int data, int pos) {
        return -1;
    }

    /**
     * Sets/Unsets bit data based on the bit-position provided.
     *
     * @param data
     *         The data to manipulate
     * @param pos
     *         The bit position to manipulate (0 to 7)
     * @param state
     *         If >= 0, the bit will be set, otherwise the bit will be unset
     *
     * @return The resulting data of the manipulation
     */
    public static byte writeBit(byte data, int pos, int state) {
        return writeBit(data, pos, state >= 0);
    }

    /**
     * Sets/Unsets bit data based on the bit-position provided.
     *
     * @param data
     *         The data to manipulate
     * @param pos
     *         The bit position to manipulate (0 to 7)
     * @param state
     *         True to set the bit otherwise False to unset
     *
     * @return The resulting data of the manipulation
     */
    public static byte writeBit(byte data, int pos, boolean state) {
        pos &= 0x7; //limit values to 0 and 7 only
        if (state)
            data |= 1 << pos;
        else
            data &= ~(1 << pos);
        return data;
    }

    /**
     * Converts short value to binary string
     *
     * @param value
     *         The short value to convert
     *
     * @return The binary representation of the number
     */
    public static String toBinary(int value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            short mask = (short) (1 << i);
            sb.append((value & mask) != 0 ? "1" : "0");
        }
        return sb.toString();
    }

    /**
     * Converts short value to binary string
     *
     * @param value
     *         The short value to convert
     *
     * @return The binary representation of the number
     */
    public static String toBinary(short value) {
        StringBuilder sb = new StringBuilder();
        for (int i = 15; i >= 0; i--) {
            short mask = (short) (1 << i);
            sb.append((value & mask) != 0 ? "1" : "0");
        }
        return sb.toString();
    }
}
