package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.GlcdDriverFactory;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.core.util.concurrent.ThreadUtils;
import com.ibasco.pidisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.pidisplay.drivers.glcd.GlcdDriver;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdFont;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class DrawTestService extends Service<Void> {

    private static final Logger log = LoggerFactory.getLogger(DrawTestService.class);

    private GlcdDriver driver;

    private AtomicBoolean invalidated = new AtomicBoolean(false);

    private ObjectProperty<GlcdDisplay> display = new SimpleObjectProperty<GlcdDisplay>() {
        @Override
        protected void invalidated() {
            invalidated.compareAndSet(false, true);
        }
    };

    private ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<GlcdBusInterface>() {
        @Override
        protected void invalidated() {
            invalidated.compareAndSet(false, true);
        }
    };

    private ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<PixelBuffer>() {
        @Override
        protected void invalidated() {
            invalidated.compareAndSet(false, true);
        }
    };

    public DrawTestService() {
        setExecutor(Context.getTaskExecutor());
    }

    @Override
    public void start() {
        if (isRunning()) {
            log.debug("Running already");
            return;
        }
        super.start();
    }

    private void refreshDriver() {
        if (display.get() == null)
            throw new NullPointerException("Controller cannot be null");
        if (busInterface.get() == null)
            throw new NullPointerException("Bus interface is not specified");
        if (buffer.get() == null)
            throw new NullPointerException("Buffer is not specified");
        driver = GlcdDriverFactory.createVirtual(display.get(), busInterface.get(), buffer.get());
    }

    @Override
    protected Task<Void> createTask() {

        if (driver == null || invalidated.getAndSet(false)) {
            log.debug("Refreshing virtual driver");
            refreshDriver();
        }

        GlcdEmulator emulator = driver.getDriverEventHandler();
        emulator.reset();

        return new Task<Void>() {

            private int ctr = 0;

            @Override
            protected Void call() {
                buffer.get().clear();
                log.debug("Starting drawing task (Display = {}, Bus = {})", display.get().getName(), busInterface.get());
                GlcdEmulator emulator = driver.getDriverEventHandler();
                emulator.reset();
                while (!isCancelled()) {
                    driver.setFont(GlcdFont.FONT_10X20_ME);
                    driver.clearBuffer();
                    driver.drawString(10, 32, "Test: " + String.valueOf(++ctr));
                    driver.sendBuffer();
                    if (ctr > 100)
                        ctr = 0;
                    ThreadUtils.sleep(10);
                }
                return null;
            }
        };
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
}
