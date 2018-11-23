package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.utils.PixelBuffer;

abstract public class BufferStrategyBase implements BufferStrategy {

    private PixelBuffer buffer;

    @Override
    public void setBuffer(PixelBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public PixelBuffer getBuffer() {
        return this.buffer;
    }
}
