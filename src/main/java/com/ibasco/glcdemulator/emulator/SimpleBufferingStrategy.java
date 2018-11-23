package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.utils.ByteUtils;

public class SimpleBufferingStrategy extends BufferStrategyBase {
    @Override
    public void processByte(byte data) {
        getBuffer().write(ByteUtils.reverse(data));
    }

    @Override
    public void reset() {
        getBuffer().reset();
    }
}
