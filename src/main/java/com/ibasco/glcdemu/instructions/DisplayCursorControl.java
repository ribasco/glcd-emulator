package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;

public class DisplayCursorControl extends GlcdInstruction {
    public DisplayCursorControl(byte value) {
        super(0x10, "Display/Cursor Control", value);
    }
}
