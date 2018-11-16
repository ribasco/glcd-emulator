package com.ibasco.glcdemulator.exceptions;

public class EmulatorControllerException extends RuntimeException {
    public EmulatorControllerException() {
    }

    public EmulatorControllerException(String message) {
        super(message);
    }

    public EmulatorControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmulatorControllerException(Throwable cause) {
        super(cause);
    }

    public EmulatorControllerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
