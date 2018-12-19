/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: ByteListenerTask.java
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
package com.ibasco.glcdemulator.net;

import com.ibasco.glcdemulator.enums.ServiceMode;
import com.ibasco.glcdemulator.services.DisplayService;
import com.ibasco.glcdemulator.utils.ByteProcessStats;
import com.ibasco.glcdemulator.utils.GlcdByteProcessor;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all listener tasks managed by the {@link DisplayService}
 *
 * @author Rafael Ibasco
 */
abstract public class ByteListenerTask extends Task<Void> {

    private static final Logger log = LoggerFactory.getLogger(ByteListenerTask.class);

    //<editor-fold desc="Properties">
    private BooleanProperty connected = new SimpleBooleanProperty();

    private ObjectProperty<ListenerOptions> listenerOptions = new SimpleObjectProperty<>();

    private final ObjectProperty<ServiceMode> serviceMode = new SimpleObjectProperty<>();

    private final ObjectProperty<GlcdByteProcessor> byteProcessor = new SimpleObjectProperty<>();

    private final ObjectProperty<GlcdDisplay> display = new SimpleObjectProperty<>();

    private final ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<>();

    private final ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<>();

    private final ObjectProperty<ByteProcessStats> stats = new SimpleObjectProperty<>();
    //</editor-fold>

    //<editor-fold desc="Getter/Setters">
    protected ByteProcessStats getStats() {
        return stats.get();
    }

    public ObjectProperty<ByteProcessStats> statsProperty() {
        return stats;
    }

    protected void setStats(ByteProcessStats stats) {
        this.stats.set(stats);
    }

    public GlcdDisplay getDisplay() {
        return display.get();
    }

    public ObjectProperty<GlcdDisplay> displayProperty() {
        return display;
    }

    public void setDisplay(GlcdDisplay display) {
        this.display.set(display);
    }

    public GlcdBusInterface getBusInterface() {
        return busInterface.get();
    }

    public ObjectProperty<GlcdBusInterface> busInterfaceProperty() {
        return busInterface;
    }

    public void setBusInterface(GlcdBusInterface busInterface) {
        this.busInterface.set(busInterface);
    }

    public PixelBuffer getBuffer() {
        return buffer.get();
    }

    public ObjectProperty<PixelBuffer> bufferProperty() {
        return buffer;
    }

    public void setBuffer(PixelBuffer buffer) {
        this.buffer.set(buffer);
    }

    public GlcdByteProcessor getByteProcessor() {
        return byteProcessor.get();
    }

    public ObjectProperty<GlcdByteProcessor> byteProcessorProperty() {
        return byteProcessor;
    }

    public void setByteProcessor(GlcdByteProcessor byteProcessor) {
        this.byteProcessor.set(byteProcessor);
    }

    protected ServiceMode getServiceMode() {
        return serviceMode.get();
    }

    public ObjectProperty<ServiceMode> serviceModeProperty() {
        return serviceMode;
    }

    public void setServiceMode(ServiceMode serviceMode) {
        this.serviceMode.set(serviceMode);
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
    //</editor-fold>

    public ByteListenerTask() {
    }

    abstract protected void configure(ListenerOptions options) throws Exception;

    abstract protected void process() throws Exception;

    abstract protected void cleanup() throws Exception;

    protected int calculateBufferSize() {
        return (buffer.get().getWidth() * buffer.get().getHeight()) / 8;
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
        byteProcessor.get().process(data);
    }

    protected void reset() {
        if (byteProcessor.get() != null)
            byteProcessor.get().reset();
    }

    /**
     * Release resources and reset properties
     */
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
        log.error("[{}] Remote display task failed", getName(), getException());
    }

    @Override
    protected void succeeded() {
        closeAndReset();
        log.info("[{}] Remote display task completed successfully", getName());
    }

    @Override
    protected void cancelled() {
        closeAndReset();
        log.info("[{}] Remote display task cancelled", getName());
    }

    @Override
    protected Void call() throws Exception {
        try {
            log.info("[{}] Created remote display task", getName());
            reset();

            log.info("[{}] Configuring remote display task", getName());
            configure(this.listenerOptions.get());

            process();
            log.info("[{}] Remote display task exited gracefully", getName());
        } finally {
            reset();
        }
        return null;
    }
}
