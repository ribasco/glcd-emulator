package com.ibasco.glcdemu.emulators;

import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.drivers.glcd.GlcdDisplay;
import javafx.beans.property.ObjectProperty;

public interface GlcdEmulator {
    /**
     * Method that is responsible for processing a single byte of data which later gets translated into an instruction
     *
     * @param data
     *         An integer representing an unsigned byte
     */
    void processByte(int data);

    PixelBuffer getBuffer();

    void setBuffer(PixelBuffer buffer);

    /**
     * Get a list of supported display configurations of this emulator.
     *
     * @return An array of {@link GlcdDisplay}
     */
    GlcdDisplay[] getSupportedDisplays();

    ObjectProperty<PixelBuffer> bufferProperty();
}
