package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;

public class DisplayHome extends GlcdInstruction {
    public DisplayHome(byte value) {
        super(0x2, "Home", value);
    }
}
