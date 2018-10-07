package com.ibasco.glcdemu.emulator;

import com.ibasco.pidisplay.core.u8g2.U8g2GpioEvent;

/**
 * Bus interface decoder. Decodes GPIO events into a recognizable byte format.
 *
 * @author Rafael Ibasco
 */
public interface GlcdBusDecoder {
    Byte decode(U8g2GpioEvent event);
}
