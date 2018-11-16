package com.ibasco.glcdemulator.exceptions;

public class GlcdEmulatorException extends RuntimeException {
    public GlcdEmulatorException() {
    }

    public GlcdEmulatorException(String message) {
        super(message);
    }

    public GlcdEmulatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlcdEmulatorException(Throwable cause) {
        super(cause);
    }

    public GlcdEmulatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
