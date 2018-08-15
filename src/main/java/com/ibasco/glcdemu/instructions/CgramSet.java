package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;

public class CgramSet extends GlcdInstruction {
    public CgramSet(byte value) {
        super(0x40, "CGRAM Set", value);
    }
}
