package com.ibasco.glcdemu.emulators.st7920;

import com.ibasco.glcdemu.emulators.GlcdInstruction;

/**
 * Base class for ST7920 instruction
 *
 * @author Rafael Ibasco
 */
abstract public class ST7920Instruction extends GlcdInstruction<ST7920InstructionFlags> {
    public ST7920Instruction(ST7920InstructionFlags flag, byte value) {
        super(flag, value);
    }

    @Override
    public ST7920InstructionFlags getFlag() {
        return super.getFlag();
    }
}
