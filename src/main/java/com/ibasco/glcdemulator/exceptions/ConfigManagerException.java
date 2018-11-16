package com.ibasco.glcdemulator.exceptions;

public class ConfigManagerException extends RuntimeException {
    public ConfigManagerException() {
    }

    public ConfigManagerException(String message) {
        super(message);
    }

    public ConfigManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigManagerException(Throwable cause) {
        super(cause);
    }

    public ConfigManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
