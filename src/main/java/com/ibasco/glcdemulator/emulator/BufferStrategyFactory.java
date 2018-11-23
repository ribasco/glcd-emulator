package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.exceptions.BufferStrategyFactoryException;

public class BufferStrategyFactory {
    public static BufferStrategy createBufferStrategy(Class<? extends GlcdEmulator> emulatorClass) {
        GlcdBufferStrategy bufferStrategy = emulatorClass.getAnnotation(Emulator.class).bufferStrategy();
        return createBufferStrategy(bufferStrategy);
    }

    public static BufferStrategy createBufferStrategy(GlcdBufferStrategy strategy) {
        Class<? extends BufferStrategy> clsBufferStrategy = strategy.getStrategyClass();
        BufferStrategy bufferStrategyInstance;
        try {
            bufferStrategyInstance = clsBufferStrategy.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BufferStrategyFactoryException(e);
        }
        return bufferStrategyInstance;
    }
}
