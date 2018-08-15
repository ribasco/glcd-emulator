package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;

public class DisplayControl extends GlcdInstruction {
    public DisplayControl(byte value) {
        super(0x8, "Display Control", value);
    }
}
