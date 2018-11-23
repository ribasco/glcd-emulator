package com.ibasco.glcdemulator.emulator;

public enum GlcdBufferStrategy {
    PAGED_BUFFERING(PagedBufferingStrategy.class),
    SIMPLE_BUFFERING(SimpleBufferingStrategy.class);

    private Class<? extends BufferStrategy> getStrategyClass;

    GlcdBufferStrategy(Class<? extends BufferStrategy> cls) {
        this.getStrategyClass = cls;
    }

    Class<? extends BufferStrategy> getStrategyClass() {
        return getStrategyClass;
    }
}
