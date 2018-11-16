package com.ibasco.glcdemulator.exceptions;

public class BeanUtilsException extends RuntimeException {
    public BeanUtilsException() {
    }

    public BeanUtilsException(String message) {
        super(message);
    }

    public BeanUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanUtilsException(Throwable cause) {
        super(cause);
    }

    public BeanUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
