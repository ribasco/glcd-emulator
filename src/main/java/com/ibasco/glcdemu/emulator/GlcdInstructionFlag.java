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
