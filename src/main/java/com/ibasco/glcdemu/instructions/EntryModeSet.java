package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;

public class EntryModeSet extends GlcdInstruction {
    public EntryModeSet(byte value) {
        super(0x4, "Entry Mode Set", value);
    }
}
