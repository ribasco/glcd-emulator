/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdInstructionProcessor.java
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

import com.ibasco.glcdemulator.emulator.GlcdEmulator;
import com.ibasco.glcdemulator.emulator.GlcdEmulatorFactory;
import com.ibasco.ucgdisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.ucgdisplay.core.u8g2.U8g2Message;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link GlcdByteProcessor} which interprets incoming bytes as GLCD instructions
 *
 * @author Rafael Luis Ibasco
 */
public class GlcdInstructionProcessor extends GlcdByteProcessor {
    private static final int MSG_START = 0xFE;

    private static final int MSG_DC_0 = 0xE0;

    private static final int MSG_DC_1 = 0xE8;

    private static final int MSG_BYTE_SEND = 0xEC;

    private final AtomicBoolean processBytes = new AtomicBoolean(false);

    private boolean collectData = false;

    private int collectSize = -1;

    private ByteBuffer tmpBuffer = ByteBuffer.allocate(256).order(ByteOrder.LITTLE_ENDIAN);

    public static final Logger log = LoggerFactory.getLogger(GlcdInstructionProcessor.class);

    private GlcdEmulator emulator;

    private GlcdEmulator createEmulator() {
        return GlcdEmulatorFactory.createFrom(getDisplay(), getBusInterface(), getBuffer());
    }

    @Override
    public void initialize() {
        this.emulator = createEmulator();
    }

    @Override
    public void process(byte data) {
        try {
            if (emulator == null)
                throw new IllegalStateException("Emulator not provided");

            ByteProcessStats stats = getStats();
            int value = Byte.toUnsignedInt(data);

            //If the current byte is not the start byte, skip
            if (value == MSG_START && !collectData) {
                if (stats.getBytesPerFrame().get() > 0) {
                    int frameSize = stats.getBytesPerFrame().getAndSet(0);
                    if (stats.getFrameSize() != frameSize)
                        Platform.runLater(() -> stats.setFrameSize(frameSize));
                    stats.getFpsCounter().count();
                }
                processBytes.set(true);
                pulseCounters();
                return;
            }

            if (processBytes.get()) {
                U8g2ByteEvent event = null;

                if (collectData) {
                    if (collectSize == -1) {
                        collectSize = Byte.toUnsignedInt(data);
                        return;
                    }

                    tmpBuffer.put(data);
                    countBytes();

                    //check if we have collected the expected number of bytes
                    if (collectSize == tmpBuffer.position()) {
                        //start processing the buffer
                        try {
                            tmpBuffer.flip();
                            while (tmpBuffer.hasRemaining()) {
                                int tmp = Byte.toUnsignedInt(tmpBuffer.get());
                                event = new U8g2ByteEvent(U8g2Message.U8X8_MSG_BYTE_SEND, tmp);
                                emulator.onByteEvent(event);
                                pulseCounters();
                            }
                        } finally {
                            tmpBuffer.clear();
                            collectSize = -1;
                            collectData = false;
                        }
                    }
                    return;
                }

                if (value == MSG_DC_0) {
                    event = new U8g2ByteEvent(U8g2Message.U8X8_MSG_BYTE_SET_DC, 0);
                } else if (value == MSG_DC_1) {
                    event = new U8g2ByteEvent(U8g2Message.U8X8_MSG_BYTE_SET_DC, 1);
                } else if (value == MSG_BYTE_SEND) {
                    collectData = true;
                    tmpBuffer.clear();
                }

                if (event != null) {
                    emulator.onByteEvent(event);
                }

                countBytes();
            }

            pulseCounters();
        } catch (Exception e) {
            log.error("Problem occured during byte processing", e);
            processBytes.set(false);
            getStats().getBytesPerFrame().set(0);
            collectData = false;
        }
    }

    @Override
    public void reset() {
        log.info("Resetting emulator properties");
        if (emulator != null) {
            emulator.reset();
        }
        processBytes.set(false);
        getStats().getBytesPerFrame().set(0);
        getStats().getFpsCounter().reset();
        getStats().getByteCounter().reset();
        Platform.runLater(() -> getStats().setFrameSize(0));
    }

    private void countBytes() {
        getStats().getBytesPerFrame().getAndIncrement();
        getStats().getByteCounter().count();
    }

    private void pulseCounters() {
        getStats().getByteCounter().pulse();
        getStats().getFpsCounter().pulse();
    }
}
