package com.ibasco.glcdemulator.exceptions;

public class BufferStrategyFactoryException extends RuntimeException {
    public BufferStrategyFactoryException() {
    }

    public BufferStrategyFactoryException(String message) {
        super(message);
    }

    public BufferStrategyFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public BufferStrategyFactoryException(Throwable cause) {
        super(cause);
    }

    public BufferStrategyFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
