package com.ibasco.glcdemu.emulator;

import com.ibasco.pidisplay.core.u8g2.U8g2GpioEvent;

public interface GlcdCommEncoder {
    U8g2GpioEvent encode(byte data);
}
