package com.ibasco.glcdemu.emulator.st7920.instructions;

import com.ibasco.glcdemu.emulator.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulator.st7920.ST7920InstructionFlag;

public class FunctionSet extends ST7920Instruction {
    public FunctionSet(byte value) {
        super(ST7920InstructionFlag.FUNCTION_SET, value);
    }
}
