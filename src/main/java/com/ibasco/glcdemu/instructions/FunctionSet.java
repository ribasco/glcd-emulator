package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;

public class FunctionSet extends GlcdInstruction {
    public FunctionSet(byte value) {
        super(0x20, "Function Set", value);
    }
}
