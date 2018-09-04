package com.ibasco.glcdemu.emulator.st7920.instructions;

import com.ibasco.glcdemu.emulator.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulator.st7920.ST7920InstructionFlags;

public class CgramSet extends ST7920Instruction {
    public CgramSet(byte value) {
        super(ST7920InstructionFlags.CGRAM_SET, value);
    }
}
