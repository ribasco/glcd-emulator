package com.ibasco.glcdemulator.exceptions;

public class StaticInitializationException extends RuntimeException {
    public StaticInitializationException() {
    }

    public StaticInitializationException(String message) {
        super(message);
    }

    public StaticInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaticInitializationException(Throwable cause) {
        super(cause);
    }

    public StaticInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
