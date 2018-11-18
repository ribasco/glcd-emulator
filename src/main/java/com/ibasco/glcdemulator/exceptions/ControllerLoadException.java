package com.ibasco.glcdemulator.exceptions;

public class ControllerLoadException extends RuntimeException {
    public ControllerLoadException() {
    }

    public ControllerLoadException(String message) {
        super(message);
    }

    public ControllerLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerLoadException(Throwable cause) {
        super(cause);
    }

    public ControllerLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
