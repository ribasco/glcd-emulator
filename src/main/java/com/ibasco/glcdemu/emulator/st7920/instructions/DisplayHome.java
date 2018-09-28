package com.ibasco.glcdemu.emulator.st7920.instructions;

import com.ibasco.glcdemu.emulator.st7920.ST7920Instruction;

import static com.ibasco.glcdemu.emulator.st7920.ST7920InstructionFlag.HOME;

public class DisplayHome extends ST7920Instruction {
    public DisplayHome(byte value) {
        super(HOME, value);
    }
}
