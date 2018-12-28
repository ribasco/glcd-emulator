/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdBufferProcessor.java
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


import com.ibasco.glcdemulator.emulator.BufferLayout;
import com.ibasco.glcdemulator.emulator.BufferLayoutFactory;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link GlcdByteProcessor} which interprets incoming bytes as data which will directly be written to the display buffer.
 *
 * @author Rafael Luis Ibasco
 */
public class GlcdBufferProcessor extends GlcdByteProcessor {

    public static final Logger log = LoggerFactory.getLogger(GlcdBufferProcessor.class);

    private BufferLayout bufferLayout;

    private int processedBytes = 0;

    @Override
    public void initialize() {
        this.bufferLayout = BufferLayoutFactory.createBufferLayout(getDisplay(), getBuffer());
        this.processedBytes = 0;
        getStats().getBytesPerFrame().set(getBuffer().size());
        log.info("Initializing buffer layout: {}", bufferLayout);
    }

    @Override
    public void process(byte data) {
        if (bufferLayout == null)
            return;

        ByteProcessStats stats = this.getStats();
        bufferLayout.processByte(data);
        countBytes();

        //have we reached the end of buffer?
        if (++processedBytes == getBuffer().size()) {
            int bytesPerFrame = stats.getBytesPerFrame().getAndSet(0);
            if (stats.getFrameSize() != bytesPerFrame)
                Platform.runLater(() -> stats.setFrameSize(bytesPerFrame));
            stats.getFpsCounter().count();
            processedBytes = 0;
        }

        pulseCounters();
    }

    private void pulseCounters() {
        getStats().getByteCounter().pulse();
        getStats().getFpsCounter().pulse();
    }

    private void countBytes() {
        getStats().getBytesPerFrame().getAndIncrement();
        getStats().getByteCounter().count();
    }

    @Override
    public void reset() {
        this.processedBytes = 0;
        bufferLayout.reset();
        getStats().getBytesPerFrame().set(0);
        getStats().getFpsCounter().reset();
        getStats().getByteCounter().reset();
        Platform.runLater(() -> getStats().setFrameSize(0));
    }
}
