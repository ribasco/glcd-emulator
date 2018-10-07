package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.drivers.glcd.GlcdDriverEventHandler;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.beans.property.ObjectProperty;

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

    ObjectProperty<PixelBuffer> bufferProperty();

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

    ObjectProperty<GlcdBusInterface> busInterfaceProperty();

    /**
     * Clears the current buffer and resets the properties back to their initial state.
     */
    void reset();
}
