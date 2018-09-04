package com.ibasco.glcdemu.emulator;

/**
 * An interface representing an instruction of the display.
 *
 * @author Rafael Ibasco
 */
public interface GlcdInstructionFlag {
    int getCode();

    String getDescription();

    GlcdInstructionFlag valueOf(int code);

    default int getBitPos(int flag) {
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
        int flagBitPos = getBitPos(getCode());
        if (flagBitPos <= -1)
            return false;
        byte lhs = (byte) (value >> flagBitPos);
        byte rhs = (byte) (getCode() >> flagBitPos);
        return lhs == rhs;
    }
}
