package com.ibasco.glcdemu.emulators.st7920.instructions;

import com.ibasco.glcdemu.emulators.st7920.ST7920Instruction;

import static com.ibasco.glcdemu.emulators.st7920.ST7920InstructionFlags.HOME;

public class DisplayHome extends ST7920Instruction {
    public DisplayHome(byte value) {
        super(HOME, value);
    }
}
