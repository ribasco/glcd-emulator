package com.ibasco.glcdemu.emulators.st7920.instructions;

import com.ibasco.glcdemu.emulators.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulators.st7920.ST7920InstructionFlags;

public class EntryModeSet extends ST7920Instruction {
    public EntryModeSet(byte value) {
        super(ST7920InstructionFlags.ENTRY_MODE_SET, value);
    }
}
