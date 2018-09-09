package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.drivers.glcd.GlcdDisplay;
import javafx.beans.property.ObjectProperty;

/**
 * Interface for emulators
 *
 * @author Rafael Ibasco
 */
public interface GlcdEmulator {
    /**
     * Method that is responsible for processing a single byte of data which later gets translated into an instruction
     *
     * @param data
     *         An integer representing an unsigned byte
     */
    void processByte(int data);

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
     * Returns an array of supported display configurations of this emulator.
     *
     * @return An array of {@link GlcdDisplay}
     */
    GlcdDisplay[] getSupportedDisplays();

    ObjectProperty<PixelBuffer> bufferProperty();

    /**
     * Clears the current buffer and resets the properties back to their initial state.
     */
    void reset();
}
