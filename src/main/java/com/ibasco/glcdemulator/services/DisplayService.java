/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
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
import com.ibasco.glcdemulator.emulator.GlcdEmulator;
import com.ibasco.glcdemulator.emulator.GlcdEmulatorFactory;
import com.ibasco.glcdemulator.enums.ConnectionType;
import com.ibasco.glcdemulator.enums.ServiceMode;
import com.ibasco.glcdemulator.exceptions.CreateListenerTaskException;
import com.ibasco.glcdemulator.net.DisplayListenerTask;
import com.ibasco.glcdemulator.net.ListenerOptions;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.lang.reflect.InvocationTargetException;

/**
 * A background service that listens on a transport interface and process display related instructions.
 *
 * @author Rafael Ibasco
 */
public class DisplayService extends Service<Void> {

    //<editor-fold desc="Service Properties">
    private ObjectProperty<GlcdDisplay> display = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<>();

    private ObjectProperty<PixelBuffer> pixelBuffer = new SimpleObjectProperty<>();

    private ReadOnlyBooleanWrapper clientConnected = new ReadOnlyBooleanWrapper();

    private ObjectProperty<ConnectionType> connectionType = new SimpleObjectProperty<>();

    private ObjectProperty<ListenerOptions> connectionOptions = new SimpleObjectProperty<>();

    private ReadOnlyObjectWrapper<DisplayListenerTask> emulatorTask = new ReadOnlyObjectWrapper<>();

    private ObjectProperty<ServiceMode> serviceMode = new SimpleObjectProperty<>();
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public DisplayService() {
        setExecutor(Context.getTaskExecutor());
    }
    //</editor-fold>

    //<editor-fold desc="Service Getter/Setter Properties">
    public ServiceMode getServiceMode() {
        return serviceMode.get();
    }

    public ObjectProperty<ServiceMode> serviceModeProperty() {
        return serviceMode;
    }

    public void setServiceMode(ServiceMode serviceMode) {
        this.serviceMode.set(serviceMode);
    }

    public PixelBuffer getPixelBuffer() {
        return pixelBuffer.get();
    }

    public ObjectProperty<PixelBuffer> pixelBufferProperty() {
        return pixelBuffer;
    }

    public void setPixelBuffer(PixelBuffer pixelBuffer) {
        this.pixelBuffer.set(pixelBuffer);
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

    public DisplayListenerTask getEmulatorTask() {
        return emulatorTask.get();
    }

    public ReadOnlyObjectProperty<DisplayListenerTask> emulatorTaskProperty() {
        return emulatorTask.getReadOnlyProperty();
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

    private GlcdEmulator createEmulator() {
        return GlcdEmulatorFactory.createFrom(getDisplay(), getBusInterface(), getPixelBuffer());
    }

    /**
     * Factory method for constructing listener tasks based on the connection type provided
     *
     * @param connectionType
     *         The {@link ConnectionType} of this emulator
     *
     * @return A {@link DisplayListenerTask}
     */
    private DisplayListenerTask createListenerTask(ConnectionType connectionType) {
        try {
            Class<? extends DisplayListenerTask> listenerClass = connectionType.getListenerClass();
            return listenerClass.getConstructor(GlcdEmulator.class).newInstance(createEmulator());
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new CreateListenerTaskException("Error occured while instatiating listener class", e);
        }
    }

    @Override
    protected Task<Void> createTask() {
        if (connectionType.get() == null)
            throw new IllegalStateException("No listener task has been specified");
        if (connectionOptions.get() == null)
            throw new IllegalStateException("No connection options specified");
        DisplayListenerTask task = createListenerTask(connectionType.get());
        emulatorTask.set(task);
        task.listenerOptionsProperty().bind(connectionOptions);
        clientConnected.bindBidirectional(task.connectedProperty());
        return task;
    }
}
