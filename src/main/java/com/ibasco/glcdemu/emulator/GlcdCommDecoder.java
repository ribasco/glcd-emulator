package com.ibasco.glcdemu.emulator;

import com.ibasco.pidisplay.core.u8g2.U8g2GpioEvent;

/**
 * Communications Protocol Decoder. Decodes GPIO events into a recognizable byte format.
 *
 * @author Rafael Ibasco
 */
public interface GlcdCommDecoder {
    Byte decode(U8g2GpioEvent event);
}
