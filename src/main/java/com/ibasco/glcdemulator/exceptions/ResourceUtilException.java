package com.ibasco.glcdemulator.exceptions;

public class ResourceUtilException extends RuntimeException {
    public ResourceUtilException() {
    }

    public ResourceUtilException(String message) {
        super(message);
    }

    public ResourceUtilException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceUtilException(Throwable cause) {
        super(cause);
    }

    public ResourceUtilException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
