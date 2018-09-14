package com.ibasco.glcdemu.net;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.pidisplay.core.u8g2.U8g2MessageEvent;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract public class RemoteListenerTask extends Task<Void> {

    private static final Logger log = LoggerFactory.getLogger(RemoteListenerTask.class);

    private ReadOnlyObjectWrapper<GlcdEmulator> emulator = new ReadOnlyObjectWrapper<>();

    private BooleanProperty connected = new SimpleBooleanProperty();

    private ObjectProperty<ListenerOptions> listenerOptions = new SimpleObjectProperty<>();

    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    public RemoteListenerTask(GlcdEmulator emulator) {
        this.emulator.set(emulator);
    }

    protected U8g2ByteEvent createEvent(byte msg, byte value) {
        U8g2ByteEvent event = new U8g2ByteEvent(msg, value);
        if (event.getMessage() == null)
            return null;
        return event;
    }

    protected void processMessage(byte msg, byte value) {
        U8g2ByteEvent event = createEvent(msg, value);
        if (event != null)
            processMessage(event);
    }

    protected void processMessage(U8g2MessageEvent event) {
        switch (event.getMessage()) {
            case U8X8_MSG_START:
                break;
            case U8X8_MSG_END:
                break;
            case U8X8_MSG_BYTE_SEND:
                this.emulator.get().processByte(event.getValue());
                break;
        }
    }

    protected final int calculateBufferSize() {
        PixelBuffer displayBuffer = getEmulator().getBuffer();
        return (((displayBuffer.getWidth() * displayBuffer.getHeight()) / 8) * 2) + 512;
    }

    protected String getName() {
        return "-";
    }

    @Override
    protected void updateMessage(String message) {
        super.updateMessage(String.format("%s [%s] %s", formatter.format(LocalDateTime.now()), getName(), message));
    }

    @Override
    protected void updateValue(Void value) {
        super.updateValue(value);
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

    abstract protected void processOptions(ListenerOptions options) throws Exception;

    abstract protected void process() throws Exception;

    abstract protected void closeResources() throws Exception;

    protected void reset() {
        emulator.get().reset();
        updateMessage("Emulator properties reset");
    }

    private void closeAndReset() {
        try {
            closeResources();
        } catch (Exception e) {
            log.error("Problem closing resources in " + getClass().getSimpleName(), e);
            updateMessage("Problem closing resources in " + getClass().getSimpleName());
        }
        setConnected(false);
        reset();
    }

    @Override
    protected void failed() {
        closeAndReset();
        log.error("Something went wrong with the service", getException());
    }

    @Override
    protected void succeeded() {
        closeAndReset();
    }

    @Override
    protected void cancelled() {
        closeAndReset();
    }

    @Override
    protected Void call() throws Exception {
        try {
            if (emulator.get() == null)
                throw new NullPointerException("Emulator cannot be null");
            processOptions(this.listenerOptions.get());
            updateMessage("Starting task");
            process();
            updateMessage("Exiting task");
        } finally {
            reset();
        }
        return null;
    }
}
