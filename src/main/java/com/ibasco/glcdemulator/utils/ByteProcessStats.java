/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: ByteProcessStats.java
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
package com.ibasco.glcdemulator.utils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.concurrent.atomic.AtomicInteger;

public class ByteProcessStats {
    private final Counter fpsCounter = new Counter();

    private final Counter byteCounter = new Counter();

    private final IntegerProperty frameSize = new SimpleIntegerProperty(0);

    private final AtomicInteger bytesPerFrame = new AtomicInteger();

    public AtomicInteger getBytesPerFrame() {
        return bytesPerFrame;
    }

    public Counter getFpsCounter() {
        return fpsCounter;
    }

    public Counter getByteCounter() {
        return byteCounter;
    }

    public int getFrameSize() {
        return frameSize.get();
    }

    public void setFrameSize(int frameSize) {
        this.frameSize.set(frameSize);
    }

    public ReadOnlyIntegerProperty fpsCounterProperty() {
        return fpsCounter.lastCountProperty();
    }

    public ReadOnlyIntegerProperty bpsCountProperty() {
        return byteCounter.lastCountProperty();
    }

    public IntegerProperty frameSizeProperty() {
        return frameSize;
    }

    public void reset() {
        fpsCounter.reset();
        byteCounter.reset();
        bytesPerFrame.set(0);
    }
}
