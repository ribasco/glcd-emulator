package com.ibasco.glcdemulator.exceptions;

public class EmulatorFactoryException extends RuntimeException {
    public EmulatorFactoryException() {
    }

    public EmulatorFactoryException(String message) {
        super(message);
    }

    public EmulatorFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmulatorFactoryException(Throwable cause) {
        super(cause);
    }

    public EmulatorFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
