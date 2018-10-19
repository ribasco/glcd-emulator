package com.ibasco.glcdemu.emulator;

import com.ibasco.ucgdisplay.core.u8g2.U8g2GpioEvent;

/**
 * Converts bytes to {@link U8g2GpioEvent}
 *
 * @author Rafael Ibasco
 */
public interface GlcdBusEncoder {
    U8g2GpioEvent encode(byte data);
}
