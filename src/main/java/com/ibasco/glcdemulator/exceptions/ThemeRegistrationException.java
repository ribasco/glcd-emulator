package com.ibasco.glcdemulator.exceptions;

public class ThemeRegistrationException extends RuntimeException {
    public ThemeRegistrationException() {
    }

    public ThemeRegistrationException(String message) {
        super(message);
    }

    public ThemeRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThemeRegistrationException(Throwable cause) {
        super(cause);
    }

    public ThemeRegistrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
