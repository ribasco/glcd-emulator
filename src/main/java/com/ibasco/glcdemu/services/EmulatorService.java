package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.enums.ConnectionType;
import com.ibasco.glcdemu.net.ListenerOptions;
import com.ibasco.glcdemu.net.RemoteListenerTask;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * A background service that listens on the network for emulator instructions and processes it accordingly.
 *
 * @author Rafael Ibasco
 */
public class EmulatorService extends Service<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmulatorService.class);

    //<editor-fold desc="Service Properties">
    private ObjectProperty<GlcdEmulator> emulator = new SimpleObjectProperty<>();

    private ReadOnlyBooleanWrapper clientConnected = new ReadOnlyBooleanWrapper();

    private ObjectProperty<ConnectionType> connectionType = new SimpleObjectProperty<>();

    private ObjectProperty<ListenerOptions> connectionOptions = new SimpleObjectProperty<>();
    //</editor-fold>

    public EmulatorService() {
        setExecutor(Context.getTaskExecutor());
    }

    //<editor-fold desc="Service Getter/Setter Properties">
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

    public GlcdEmulator getEmulator() {
        return emulator.get();
    }

    public ObjectProperty<GlcdEmulator> emulatorProperty() {
        return emulator;
    }

    public void setEmulator(GlcdEmulator emulator) {
        this.emulator.set(emulator);
    }
    //</editor-fold>

    /**
     * Factory method to create listener tasks based on the connection type
     *
     * @param connectionType
     *         The {@link ConnectionType} of this emulator
     *
     * @return A {@link RemoteListenerTask}
     */
    private RemoteListenerTask createListenerTask(ConnectionType connectionType) {
        try {
            Class<? extends RemoteListenerTask> listenerClass = connectionType.getListenerClass();
            return listenerClass.getConstructor(GlcdEmulator.class).newInstance(emulator.get());
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Error occured while instatiating listener class", e);
        }
    }

    private ChangeListener<String> taskMessageListener;

    public void setTaskMessageListener(ChangeListener<String> listener) {
        this.taskMessageListener = listener;
    }

    @Override
    protected Task<Void> createTask() {
        if (connectionType.get() == null)
            throw new IllegalStateException("No listener task has been specified");
        if (connectionOptions.get() == null)
            throw new IllegalStateException("No connection options specified");
        RemoteListenerTask task = createListenerTask(connectionType.get());
        task.listenerOptionsProperty().bind(connectionOptions);
        clientConnected.bindBidirectional(task.connectedProperty());
        if (taskMessageListener != null) {
            task.messageProperty().addListener(taskMessageListener);
        }
        return task;
    }
}
