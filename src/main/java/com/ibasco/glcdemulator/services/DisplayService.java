/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: DisplayService.java
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
package com.ibasco.glcdemulator.services;

import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.enums.ConnectionType;
import com.ibasco.glcdemulator.enums.ServiceMode;
import com.ibasco.glcdemulator.exceptions.CreateListenerTaskException;
import com.ibasco.glcdemulator.net.ByteListenerTask;
import com.ibasco.glcdemulator.net.ListenerOptions;
import com.ibasco.glcdemulator.utils.ByteProcessStats;
import com.ibasco.glcdemulator.utils.GlcdByteProcessor;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdSetupInfo;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * A background service that listens on a transport interface and process display related instructions.
 *
 * @author Rafael Ibasco
 */
@SuppressWarnings("WeakerAccess")
public class DisplayService extends Service<Void> {

    public static final Logger log = LoggerFactory.getLogger(DisplayService.class);

    //<editor-fold desc="Service Properties">
    private ObjectProperty<GlcdDisplay> display = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<>();

    private ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<>();

    private ReadOnlyBooleanWrapper clientConnected = new ReadOnlyBooleanWrapper();

    private ObjectProperty<ConnectionType> connectionType = new SimpleObjectProperty<>();

    private ObjectProperty<ListenerOptions> connectionOptions = new SimpleObjectProperty<>();

    private ReadOnlyObjectWrapper<ByteListenerTask> displayTask = new ReadOnlyObjectWrapper<>();

    private ObjectProperty<ServiceMode> serviceMode = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdByteProcessor> byteProcessor = new SimpleObjectProperty<>();

    private ObjectProperty<ByteProcessStats> statistics = new SimpleObjectProperty<>(new ByteProcessStats());
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public DisplayService() {
        setExecutor(Context.getTaskExecutor());
    }
    //</editor-fold>

    //<editor-fold desc="Service Getter/Setter Properties">
    public ByteProcessStats getStatistics() {
        return statistics.get();
    }

    public ObjectProperty<ByteProcessStats> statisticsProperty() {
        return statistics;
    }

    public void setStatistics(ByteProcessStats statistics) {
        this.statistics.set(statistics);
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

    public ServiceMode getServiceMode() {
        return serviceMode.get();
    }

    public ObjectProperty<ServiceMode> serviceModeProperty() {
        return serviceMode;
    }

    public void setServiceMode(ServiceMode serviceMode) {
        this.serviceMode.set(serviceMode);
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

    public ByteListenerTask getDisplayTask() {
        return displayTask.get();
    }

    public ReadOnlyObjectProperty<ByteListenerTask> displayTaskProperty() {
        return displayTask.getReadOnlyProperty();
    }

    public ListenerOptions getConnectionOptions() {
        return connectionOptions.get();
    }

    public ObjectProperty<ListenerOptions> connectionOptionsProperty() {
        return connectionOptions;
    }

    public void setConnectionOptions(ListenerOptions connectionOptions) {
        this.connectionOptions.set(connectionOptions);
    }

    public ConnectionType getConnectionType() {
        return connectionType.get();
    }

    public ObjectProperty<ConnectionType> connectionTypeProperty() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType.set(connectionType);
    }

    public boolean isClientConnected() {
        return clientConnected.get();
    }

    public ReadOnlyBooleanProperty clientConnectedProperty() {
        return clientConnected.getReadOnlyProperty();
    }

    private void setClientConnected(boolean value) {
        Platform.runLater(() -> clientConnected.set(value));
    }
    //</editor-fold>

    /**
     * Factory method for constructing listener tasks based on the connection type provided
     *
     * @param connectionType
     *         The {@link ConnectionType} of this emulator
     *
     * @return A {@link ByteListenerTask}
     */
    private ByteListenerTask createListenerTask(ConnectionType connectionType) {
        try {
            Class<? extends ByteListenerTask> listenerClass = connectionType.getListenerClass();
            return listenerClass.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new CreateListenerTaskException("Error occured while instatiating listener class", e);
        }
    }

    private void initByteProcessor(GlcdByteProcessor processor) {
        log.info("Initializing byte processor");
        processor.statsProperty().bind(statistics);
        processor.displayProperty().bind(display);
        processor.bufferProperty().bind(buffer);
        processor.busInterfaceProperty().bind(busInterface);
        processor.initialize();
    }

    @Override
    protected Task<Void> createTask() {
        if (connectionType.get() == null)
            throw new IllegalStateException("No listener task has been specified");
        if (connectionOptions.get() == null)
            throw new IllegalStateException("No connection options specified");
        if (display.get() == null)
            throw new IllegalStateException("Display cannot be null");
        if (ServiceMode.EMULATED.equals(getServiceMode()) && busInterface.get() == null)
            throw new IllegalStateException("Bus interface cannot be null");
        if (byteProcessor.get() == null)
            throw new IllegalStateException("Byte processor cannot be null");

        ByteListenerTask task = createListenerTask(connectionType.get());

        GlcdBusInterface bInt = getBusInterface() == null && getDisplay().getBusInterfaces().size() > 0 ? getDisplay().getBusInterfaces().get(0) : getBusInterface();
        GlcdSetupInfo setupInfo = Arrays.stream(display.get().getSetupDetails())
                .filter(setup -> busInterface.get() != null && setup.isSupported(bInt))
                .findFirst()
                .orElse(null);
        String setupFunction = setupInfo != null ? setupInfo.getFunction() : "N/A";
        log.info("Creating display service task: {} (Display = {}, Bus Interface = {}, Byte Processor = {}, Constructor = {})", task.getClass().getSimpleName(), display.get().getName(), busInterface.get() != null ? busInterface.get().getDescription() : "N/A", byteProcessor.get() != null ? byteProcessor.get().getClass().getSimpleName() : "", setupFunction);

        task.listenerOptionsProperty().bind(connectionOptions);
        task.displayProperty().bind(display);
        task.busInterfaceProperty().bind(busInterface);
        task.bufferProperty().bind(buffer);
        task.byteProcessorProperty().bind(byteProcessor);
        task.statsProperty().bind(statistics);
        initByteProcessor(byteProcessor.get());

        assert task.statsProperty().get() != null;

        displayTask.set(task);
        clientConnected.bindBidirectional(task.connectedProperty());
        return task;
    }
}
