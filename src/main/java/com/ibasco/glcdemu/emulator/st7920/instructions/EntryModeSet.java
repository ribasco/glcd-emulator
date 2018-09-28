package com.ibasco.glcdemu.emulator.st7920.instructions;

import com.ibasco.glcdemu.emulator.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulator.st7920.ST7920InstructionFlag;

public class EntryModeSet extends ST7920Instruction {
    public EntryModeSet(byte value) {
        super(ST7920InstructionFlag.ENTRY_MODE_SET, value);
    }
}
