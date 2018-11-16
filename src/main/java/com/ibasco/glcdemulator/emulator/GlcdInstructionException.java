package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.exceptions.GlcdEmulatorException;

public class GlcdInstructionException extends GlcdEmulatorException {
    public GlcdInstructionException() {
    }

    public GlcdInstructionException(String message) {
        super(message);
    }

    public GlcdInstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlcdInstructionException(Throwable cause) {
        super(cause);
    }

    public GlcdInstructionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
