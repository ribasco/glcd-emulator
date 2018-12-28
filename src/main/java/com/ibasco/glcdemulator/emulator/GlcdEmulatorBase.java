/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdEmulatorBase.java
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
package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Base class for emulators providing basic functionality
 *
 * @author Rafael Ibasco
 */
abstract public class GlcdEmulatorBase implements GlcdEmulator {

    private ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<>();

    private BufferLayout bufferStrategy;

    @Override
    public final PixelBuffer getBuffer() {
        return buffer.get();
    }

    @Override
    public final void setBuffer(PixelBuffer buffer) {
        this.buffer.set(buffer);
    }

    @Override
    public final void setBusInterface(GlcdBusInterface busInterface) {
        this.busInterface.set(busInterface);
    }

    @Override
    public final GlcdBusInterface getBusInterface() {
        return this.busInterface.get();
    }

    @Override
    public BufferLayout getBufferLayout() {
        return bufferStrategy;
    }

    @Override
    public void setBufferLayout(BufferLayout bufferStrategy) {
        this.bufferStrategy = bufferStrategy;
    }
}
