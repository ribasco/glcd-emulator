/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdByteProcessor.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 - 2019 Rafael Luis Ibasco
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
package com.ibasco.glcdemulator.utils;

import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

abstract public class GlcdByteProcessor {

    private ObjectProperty<GlcdDisplay> display = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<>();

    private ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<>();

    private ObjectProperty<ByteProcessStats> stats = new SimpleObjectProperty<>();

    protected GlcdDisplay getDisplay() {
        return display.get();
    }

    public ObjectProperty<GlcdDisplay> displayProperty() {
        return display;
    }

    protected void setDisplay(GlcdDisplay display) {
        this.display.set(display);
    }

    protected GlcdBusInterface getBusInterface() {
        return busInterface.get();
    }

    public ObjectProperty<GlcdBusInterface> busInterfaceProperty() {
        return busInterface;
    }

    protected void setBusInterface(GlcdBusInterface busInterface) {
        this.busInterface.set(busInterface);
    }

    protected PixelBuffer getBuffer() {
        return buffer.get();
    }

    public ObjectProperty<PixelBuffer> bufferProperty() {
        return buffer;
    }

    protected void setBuffer(PixelBuffer buffer) {
        this.buffer.set(buffer);
    }

    protected ByteProcessStats getStats() {
        return stats.get();
    }

    public ObjectProperty<ByteProcessStats> statsProperty() {
        return stats;
    }

    protected void setStats(ByteProcessStats stats) {
        this.stats.set(stats);
    }

    /**
     * Initialize properties
     */
    abstract public void initialize();

    /**
     * Process the byte data
     */
    abstract public void process(byte data);

    /**
     * Reset properties
     */
    abstract public void reset();
}
