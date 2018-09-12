package com.ibasco.glcdemu.net;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.pidisplay.core.u8g2.U8g2Message;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class RemoteListenerTask extends Task<Void> {

    private static final Logger log = LoggerFactory.getLogger(RemoteListenerTask.class);

    private ReadOnlyObjectWrapper<GlcdEmulator> emulator = new ReadOnlyObjectWrapper<>();

    private BooleanProperty connected = new SimpleBooleanProperty();

    private ObjectProperty<ListenerOptions> listenerOptions = new SimpleObjectProperty<>();

    public RemoteListenerTask(GlcdEmulator emulator) {
        this.emulator.set(emulator);
    }

    protected void processMessage(byte msg, byte value) {
        U8g2ByteEvent event = new U8g2ByteEvent(msg, value);
        if (U8g2Message.U8X8_MSG_BYTE_SEND.equals(event.getMessage())) {
            this.emulator.get().processByte(event.getValue());
        }
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

    protected void reset() {
        emulator.get().reset();
    }

    @Override
    protected Void call() throws Exception {
        try {
            if (emulator.get() == null)
                throw new NullPointerException("Emulator cannot be null");
            processOptions(this.listenerOptions.get());
            process();
        } finally {
            reset();
        }
        return null;
    }
}
