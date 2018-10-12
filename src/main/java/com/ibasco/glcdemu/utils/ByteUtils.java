package com.ibasco.glcdemu.utils;

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
