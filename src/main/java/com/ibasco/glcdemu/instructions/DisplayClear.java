package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;

public class DisplayClear extends GlcdInstruction {
    public DisplayClear(byte value) {
        super(0x1, "Display Clear", value);
    }
}
