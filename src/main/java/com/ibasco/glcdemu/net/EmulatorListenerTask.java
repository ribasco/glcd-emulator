package com.ibasco.glcdemu.net;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.exceptions.EmulatorProcessException;
import com.ibasco.glcdemu.utils.Counter;
import com.ibasco.glcdemu.utils.PixelBuffer;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
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

    private ReadOnlyObjectWrapper<GlcdEmulator> emulator = new ReadOnlyObjectWrapper<>();

    private BooleanProperty connected = new SimpleBooleanProperty();

    private ObjectProperty<ListenerOptions> listenerOptions = new SimpleObjectProperty<>();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private final Counter fpsCounter = new Counter();

    private final Counter byteCounter = new Counter();

    private final AtomicBoolean processBytes = new AtomicBoolean(false);

    private final AtomicInteger bytesCollected = new AtomicInteger();

    private final ReadOnlyIntegerWrapper frameSize = new ReadOnlyIntegerWrapper(0);

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

    @Override
    protected void updateMessage(String message) {
        super.updateMessage(String.format("%s [%s] %s", formatter.format(LocalDateTime.now()), getName(), message));
    }

    /**
     * Accept a byte from a data stream and pass to the internal emulator instance for further processing.
     *
     * @param data
     *         A byte of data
     */
    protected void processByte(byte data) {
        try {
            if (emulator.get() == null)
                throw new IllegalStateException("No controller has been set");
            int value = Byte.toUnsignedInt(data);

            //If the current byte is not the start byte, skip
            if (value == MSG_START) {
                if (bytesCollected.get() > 0) {
                    int frameSize = bytesCollected.getAndSet(0);
                    if (this.frameSize.get() != frameSize)
                        Platform.runLater(() -> this.frameSize.set(frameSize));
                    fpsCounter.count();
                }
                processBytes.set(true);
                pulseCounters();
                return;
            }
            if (processBytes.get()) {
                getEmulator().processByte(value);
                bytesCollected.getAndIncrement();
                byteCounter.count();
            }
            pulseCounters();
        } catch (EmulatorProcessException e) {
            log.error("Problem occured during byte processing", e);
            processBytes.set(false);
            bytesCollected.set(0);
        }
    }

    private void pulseCounters() {
        byteCounter.pulse();
        fpsCounter.pulse();
    }

    protected void reset() {
        getEmulator().reset();
        processBytes.set(false);
        bytesCollected.set(0);
        fpsCounter.reset();
        byteCounter.reset();
        Platform.runLater(() -> frameSize.set(0));
    }

    private void closeAndReset() {
        try {
            cleanup();
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
        log.error("Emulator task failed", getException());
    }

    @Override
    protected void succeeded() {
        closeAndReset();
        log.info("Emulator task completed successfully");
    }

    @Override
    protected void cancelled() {
        closeAndReset();
        log.info("Emulator task cancelled");
    }

    @Override
    protected Void call() throws Exception {
        try {
            if (emulator.get() == null)
                throw new NullPointerException("Emulator cannot be null");
            updateMessage("Configuring emulator task");
            configure(this.listenerOptions.get());
            updateMessage("Starting emulator task");
            process();
            updateMessage("Emulator task exited");
        } finally {
            reset();
        }
        return null;
    }
}
