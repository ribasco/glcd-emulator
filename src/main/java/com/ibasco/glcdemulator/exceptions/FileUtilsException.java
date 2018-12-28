/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: FileUtilsException.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Luis Ibasco
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
package com.ibasco.glcdemulator.exceptions;

public class FileUtilsException extends RuntimeException {
    public FileUtilsException() {
    }

    public FileUtilsException(String message) {
        super(message);
    }

    public FileUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUtilsException(Throwable cause) {
        super(cause);
    }

    public FileUtilsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
