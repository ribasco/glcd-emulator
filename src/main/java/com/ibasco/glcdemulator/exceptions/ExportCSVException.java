package com.ibasco.glcdemulator.exceptions;

public class ExportCSVException extends RuntimeException {
    public ExportCSVException() {
    }

    public ExportCSVException(String message) {
        super(message);
    }

    public ExportCSVException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportCSVException(Throwable cause) {
        super(cause);
    }

    public ExportCSVException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
