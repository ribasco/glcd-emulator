/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: PixelBufferException.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
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
