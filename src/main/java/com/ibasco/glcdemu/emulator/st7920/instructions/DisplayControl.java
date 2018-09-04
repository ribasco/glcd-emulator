package com.ibasco.glcdemu.emulator.st7920.instructions;

import com.ibasco.glcdemu.emulator.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulator.st7920.ST7920InstructionFlags;

public class DisplayControl extends ST7920Instruction {
    public DisplayControl(byte value) {
        super(ST7920InstructionFlags.DISPLAY_CONTROL, value);
    }
}
