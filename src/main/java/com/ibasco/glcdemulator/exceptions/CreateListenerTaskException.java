package com.ibasco.glcdemulator.exceptions;

public class CreateListenerTaskException extends DisplayServiceException {
    public CreateListenerTaskException() {
    }

    public CreateListenerTaskException(String message) {
        super(message);
    }

    public CreateListenerTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateListenerTaskException(Throwable cause) {
        super(cause);
    }

    public CreateListenerTaskException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
