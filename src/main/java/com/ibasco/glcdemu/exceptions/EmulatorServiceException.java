package com.ibasco.glcdemu.exceptions;

public class EmulatorServiceException extends Exception {
    public EmulatorServiceException() {
    }

    public EmulatorServiceException(String message) {
        super(message);
    }

    public EmulatorServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmulatorServiceException(Throwable cause) {
        super(cause);
    }

    public EmulatorServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
