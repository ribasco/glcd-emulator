package com.ibasco.glcdemu.emulator.st7920.instructions;

import com.ibasco.glcdemu.emulator.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulator.st7920.ST7920InstructionFlag;

public class DisplayClear extends ST7920Instruction {
    public DisplayClear(byte value) {
        super(ST7920InstructionFlag.DISPLAY_CLEAR, value);
    }
}
