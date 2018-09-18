package com.ibasco.glcdemu.exceptions;

import com.ibasco.glcdemu.emulator.GlcdEmulator;

public class EmulatorProcessException extends RuntimeException {

    private GlcdEmulator emulator;

    public EmulatorProcessException(GlcdEmulator emulator, String message) {
        this(emulator, message, null);
    }

    public EmulatorProcessException(GlcdEmulator emulator, String message, Throwable ex) {
        super(message, ex);
        this.emulator = emulator;
    }

    public GlcdEmulator getEmulator() {
        return emulator;
    }
}
