package com.ibasco.glcdemu.emulators.st7920.instructions;

import com.ibasco.glcdemu.emulators.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulators.st7920.ST7920InstructionFlags;

public class FunctionSet extends ST7920Instruction {
    public FunctionSet(byte value) {
        super(ST7920InstructionFlags.FUNCTION_SET, value);
    }
}
