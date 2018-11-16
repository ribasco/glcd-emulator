package com.ibasco.glcdemulator.exceptions;

public class StageHelperException extends RuntimeException {
    public StageHelperException() {
    }

    public StageHelperException(String message) {
        super(message);
    }

    public StageHelperException(String message, Throwable cause) {
        super(message, cause);
    }

    public StageHelperException(Throwable cause) {
        super(cause);
    }

    public StageHelperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
