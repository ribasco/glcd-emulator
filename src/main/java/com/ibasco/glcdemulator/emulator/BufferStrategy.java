package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.utils.PixelBuffer;

public interface BufferStrategy {
    void processByte(byte data);

    void setBuffer(PixelBuffer buffer);

    PixelBuffer getBuffer();

    /**
     * Called once during instantiation
     */
    default void initialize() {

    }

    /**
     * Reset properties
     */
    void reset();
}
