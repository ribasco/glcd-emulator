package com.ibasco.glcdemu.emulators.st7920.instructions;

import com.ibasco.glcdemu.emulators.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulators.st7920.ST7920InstructionFlags;

public class DisplayClear extends ST7920Instruction {
    public DisplayClear(byte value) {
        super(ST7920InstructionFlags.DISPLAY_CLEAR, value);
    }
}
