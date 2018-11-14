/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: EmulatorListenerTask.java
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
package com.ibasco.glcdemu.net;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.utils.Counter;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.ucgdisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.ucgdisplay.core.u8g2.U8g2Message;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all listener tasks managed by the {@link com.ibasco.glcdemu.services.EmulatorService}
 *
 * @author Rafael Ibasco
 */
abstract public class EmulatorListenerTask extends Task<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmulatorListenerTask.class);

    private static final int MSG_START = 0xFE;

    private static final int MSG_DC_0 = 0xE0;

    private static final int MSG_DC_1 = 0xE8;

    private static final int MSG_BYTE_SEND = 0xEC;

    private ReadOnlyObjectWrapper<GlcdEmulator> emulator = new ReadOnlyObjectWrapper<>();

    private BooleanProperty connected = new SimpleBooleanProperty();

    private ObjectProperty<ListenerOptions> listenerOptions = new SimpleObjectProperty<>();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private final Counter fpsCounter = new Counter();

    private final Counter byteCounter = new Counter();

    private final AtomicBoolean processBytes = new AtomicBoolean(false);

    private final AtomicInteger bytesPerFrame = new AtomicInteger();

    private final ReadOnlyIntegerWrapper frameSize = new ReadOnlyIntegerWrapper(0);

    private boolean collectData = false;

    private int collectSize = -1;

    private ByteBuffer buffer = ByteBuffer.allocate(256).order(ByteOrder.LITTLE_ENDIAN);

    public EmulatorListenerTask(GlcdEmulator emulator) {
        this.emulator.set(emulator);
    }

    protected int getFrameSize() {
        return frameSize.get();
    }

    public ReadOnlyIntegerProperty frameSizeProperty() {
        return frameSize.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty fpsCountProperty() {
        return fpsCounter.lastCountProperty();
    }

    public ReadOnlyIntegerProperty bpsCountProperty() {
        return byteCounter.lastCountProperty();
    }

    public <T> void setOption(ListenerOption<T> option, T value) {
        listenerOptions.get().put(option, value);
    }

    public ListenerOptions getListenerOptions() {
        return listenerOptions.get();
    }

    public ObjectProperty<ListenerOptions> listenerOptionsProperty() {
        return listenerOptions;
    }

    public void setListenerOptions(ListenerOptions listenerOptions) {
        this.listenerOptions.set(listenerOptions);
    }

    public boolean isConnected() {
        return connected.get();
    }

    public BooleanProperty connectedProperty() {
        return connected;
    }

    public void setConnected(boolean connected) {
        Platform.runLater(() -> this.connected.set(connected));
    }

    public GlcdEmulator getEmulator() {
        return emulatorProperty().get();
    }

    public ReadOnlyObjectProperty<GlcdEmulator> emulatorProperty() {
        return emulator.getReadOnlyProperty();
    }

    abstract protected void configure(ListenerOptions options) throws Exception;

    abstract protected void process() throws Exception;

    abstract protected void cleanup() throws Exception;

    protected final int calculateBufferSize() {
        PixelBuffer displayBuffer = getEmulator().getBuffer();
        return (((displayBuffer.getWidth() * displayBuffer.getHeight()) / 8) * 2) * 2;
    }

    protected String getName() {
        return "-";
    }

    /**
     * Accept a byte from a data stream and pass to the internal emulator instance for further processing.
     *
     * @param data
     *         A byte of data
     */
    protected void processByte(byte data) {
        try {
            GlcdEmulator emulator = getEmulator();

            if (emulator == null)
                throw new IllegalStateException("No controller has been set");

            int value = Byte.toUnsignedInt(data);

            //If the current byte is not the start byte, skip
            if (value == MSG_START && !collectData) {
                if (bytesPerFrame.get() > 0) {
                    int frameSize = bytesPerFrame.getAndSet(0);
                    if (this.frameSize.get() != frameSize)
                        Platform.runLater(() -> this.frameSize.set(frameSize));
                    fpsCounter.count();
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

                    buffer.put(data);
                    countBytes();

                    //check if we have collected the expected number of bytes
                    if (collectSize == buffer.position()) {
                        //start processing the buffer
                        try {
                            buffer.flip();
                            while (buffer.hasRemaining()) {
                                int tmp = Byte.toUnsignedInt(buffer.get());
                                event = new U8g2ByteEvent(U8g2Message.U8X8_MSG_BYTE_SEND, tmp);
                                emulator.onByteEvent(event);
                                pulseCounters();
                            }
                        } finally {
                            buffer.clear();
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
                    buffer.clear();
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
            bytesPerFrame.set(0);
            collectData = false;
        }
    }

    private void countBytes() {
        bytesPerFrame.getAndIncrement();
        byteCounter.count();
    }

    private void pulseCounters() {
        byteCounter.pulse();
        fpsCounter.pulse();
    }

    protected void reset() {
        log.info("[{}] Resetting emulator properties", getName());
        if (getEmulator() != null) {
            getEmulator().reset();
        }
        processBytes.set(false);
        bytesPerFrame.set(0);
        fpsCounter.reset();
        byteCounter.reset();
        Platform.runLater(() -> frameSize.set(0));
    }

    private void closeAndReset() {
        try {
            log.info("[{}] Task cleanup", getName());
            cleanup();
        } catch (Exception e) {
            log.error("Problem closing resources in " + getClass().getSimpleName(), e);
        }
        setConnected(false);
        reset();
    }

    @Override
    protected void failed() {
        closeAndReset();
        log.error("[{}] Emulator task failed", getName(), getException());
    }

    @Override
    protected void succeeded() {
        closeAndReset();
        log.info("[{}] Emulator task completed successfully", getName());
    }

    @Override
    protected void cancelled() {
        closeAndReset();
        log.info("[{}] Emulator task cancelled", getName());
    }

    @Override
    protected Void call() throws Exception {
        try {
            if (emulator.get() == null)
                throw new IllegalArgumentException("Emulator cannot be null");

            if (emulator.get().getBusInterface() == null)
                throw new IllegalStateException("No bus interface assigned for emulator '" + emulator.get().getClass().getSimpleName() + "'");

            log.info("[{}] Created emulator task (Controller = {}, Bus = {})", getName(), emulator.get().getClass().getSimpleName(), emulator.get().getBusInterface());
            reset();

            log.info("[{}] Configuring emulator task", getName());
            configure(this.listenerOptions.get());

            process();
            log.info("[{}] Emulator task exited gracefully (Controller = {}, Bus = {})", getName(), emulator.get().getClass().getSimpleName(), emulator.get().getBusInterface());
        } finally {
            reset();
        }
        return null;
    }
}
