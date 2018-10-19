package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriverEventHandler;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;

/**
 * Interface for emulators
 *
 * @author Rafael Ibasco
 */
public interface GlcdEmulator extends GlcdDriverEventHandler {
    /**
     * Returns the underlying display buffer of this emulator
     *
     * @return The {@link PixelBuffer} of this emulator
     */
    PixelBuffer getBuffer();

    /**
     * Sets the display buffer to be used by the emulator
     *
     * @param buffer
     *         A {@link PixelBuffer} instance
     */
    void setBuffer(PixelBuffer buffer);

    /**
     * Sets the Bus communication interface for this emulator
     *
     * @param busInterface
     *         A {@link GlcdBusInterface} enumeration
     */
    void setBusInterface(GlcdBusInterface busInterface);

    /**
     * @return Returns the {@link GlcdBusInterface} of this emulator
     */
    GlcdBusInterface getBusInterface();

    /**
     * Reset internal properties back to their initial state.
     */
    void reset();
}
