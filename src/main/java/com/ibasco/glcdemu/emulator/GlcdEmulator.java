/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdEmulator.java
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
package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriverEventHandler;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;

/**
 * Interface for emulators
 *
 * @author Rafael Ibasco
 */
public interface GlcdEmulator extends GlcdDriverEventHandler {
    /**
     * Returns the underlying display buffer of this emulator
     *
     * @return The {@link PixelBuffer} of this emulator
     */
    PixelBuffer getBuffer();

    /**
     * Sets the display buffer to be used by the emulator
     *
     * @param buffer
     *         A {@link PixelBuffer} instance
     */
    void setBuffer(PixelBuffer buffer);

    /**
     * Sets the Bus communication interface for this emulator
     *
     * @param busInterface
     *         A {@link GlcdBusInterface} enumeration
     */
    void setBusInterface(GlcdBusInterface busInterface);

    /**
     * @return Returns the {@link GlcdBusInterface} of this emulator
     */
    GlcdBusInterface getBusInterface();

    /**
     * Reset internal properties back to their initial state.
     */
    void reset();
}
