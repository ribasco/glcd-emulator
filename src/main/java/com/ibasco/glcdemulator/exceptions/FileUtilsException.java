package com.ibasco.glcdemulator.exceptions;

public class FileUtilsException extends RuntimeException {
    public FileUtilsException() {
    }

    public FileUtilsException(String message) {
        super(message);
    }

    public FileUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUtilsException(Throwable cause) {
        super(cause);
    }

    public FileUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
