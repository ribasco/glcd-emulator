/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: ByteUtils.java
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
package com.ibasco.glcdemulator.utils;

public class ByteUtils {
    public static String toHexString(byte... data) {
        return toHexString(false, data);
    }

    public static String toHexString(boolean includePrefix, byte... data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        return toHexString(sb, data, includePrefix).trim().toUpperCase();
    }

    public static String toHexString(StringBuilder sb, byte[] data) {
        return toHexString(sb, data, true);
    }

    public static String toHexString(StringBuilder sb, byte[] data, boolean includePrefix) {
        printHexBytes(sb, data, includePrefix);
        return sb.toString();
    }

    public static void printHexBytes(StringBuilder sb, byte[] data, boolean includePrefix) {
        if (includePrefix) {
            sb.append("[Size: ");
            sb.append(data.length);
            sb.append("] = ");
        }
        for (byte b : data) {
            sb.append(String.format("%02x", b));
            sb.append(" ");
        }
    }
}
