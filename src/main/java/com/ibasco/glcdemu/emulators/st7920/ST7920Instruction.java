package com.ibasco.glcdemu.emulators.st7920;

import com.ibasco.glcdemu.emulators.GlcdInstruction;

public class ST7920Instruction extends GlcdInstruction<ST7920InstructionFlags> {
    public ST7920Instruction(ST7920InstructionFlags flag, byte value) {
        super(flag, value);
    }

    @Override
    public ST7920InstructionFlags getFlag() {
        return super.getFlag();
    }
}
