package com.ibasco.glcdemu.exceptions;

import com.ibasco.glcdemu.utils.PixelBuffer;
import org.apache.commons.lang3.StringUtils;

public class PixelBufferException extends RuntimeException {
    private PixelBuffer buffer;

    public PixelBufferException(String message) {
        this(message, null);
    }

    public PixelBufferException(PixelBuffer buffer) {
        this(StringUtils.EMPTY, buffer);
    }

    public PixelBufferException(String message, PixelBuffer buffer) {
        this(message, null, buffer);
    }

    public PixelBufferException(String message, Throwable cause, PixelBuffer buffer) {
        super(message, cause);
        this.buffer = buffer;
    }

    public PixelBuffer getBuffer() {
        return buffer;
    }
}
