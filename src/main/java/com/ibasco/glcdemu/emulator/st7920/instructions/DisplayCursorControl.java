package com.ibasco.glcdemu.emulator.st7920.instructions;

import com.ibasco.glcdemu.emulator.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulator.st7920.ST7920InstructionFlags;

public class DisplayCursorControl extends ST7920Instruction {
    public DisplayCursorControl(byte value) {
        super(ST7920InstructionFlags.DISPLAY_CURSOR_CONTROL, value);
    }
}
