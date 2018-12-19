/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: DrawTestService.java
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
import com.ibasco.glcdemulator.DriverFactory;
import com.ibasco.glcdemulator.emulator.BufferLayout;
import com.ibasco.glcdemulator.emulator.BufferLayoutFactory;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.glcdemulator.utils.ResourceUtil;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriver;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriverEventHandler;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
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

    private BufferLayout bufferLayout;

    public DrawTestService() {
        setExecutor(Context.getTaskExecutor());
    }

    private File javaLogoFile;

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
            throw new IllegalStateException("Controller cannot be null");
        if (busInterface.get() == null) {
            setBusInterface(GlcdBusInterface.PARALLEL_8080);
        }
        if (buffer.get() == null)
            throw new IllegalStateException("Buffer is not specified");
        driver = DriverFactory.createVirtual(display.get(), busInterface.get(), (GlcdDriverEventHandler) null);
        bufferLayout = BufferLayoutFactory.createBufferLayout(getDisplay(), getBuffer());
        try {
            URL url = ResourceUtil.getResource("images/java-logo-small.xbm");//Context.class.getClassLoader().getResource("images/java-logo.xbm");
            if (url != null)
                javaLogoFile = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to load logo file");
        }
        bufferLayout.initialize();
        bufferLayout.reset();
    }

    @Override
    protected Task<Void> createTask() {

        if (driver == null || invalidated.getAndSet(false)) {
            log.debug("Refreshing virtual driver");
            refreshDriver();
        }

        bufferLayout.reset();

        return new Task<Void>() {
            private int xPos = 0;

            @Override
            protected Void call() throws Exception {
                buffer.get().clear();
                log.info("START: Draw test (Display = {}, Bus = {}, Buffer Layout = {})", display.get().getName(), busInterface.get(), bufferLayout.getClass().getSimpleName());

                while (!isCancelled()) {
                    driver.setFont(GlcdFont.FONT_10X20_ME);
                    driver.clearBuffer();
                    driver.drawXBM(0, 10, 32, 32, javaLogoFile);

                    int y = (driver.getHeight() / 2) + (driver.getAscent() / 2);
                    String sampleText = "This is a test";
                    int textWidth = driver.getMaxCharWidth() * sampleText.length();
                    driver.setFontMode(1);
                    driver.drawString(xPos++, y, sampleText);
                    driver.sendBuffer();

                    updateDisplayBuffer();

                    if (xPos > (driver.getWidth() + textWidth))
                        xPos = 0;

                    Thread.sleep(10);
                }
                log.info("STOP: Draw test (Display = {}, Bus = {})", display.get().getName(), busInterface.get());
                return null;
            }
        };
    }

    private void updateDisplayBuffer() {
        if (bufferLayout == null)
            throw new IllegalStateException("Buffer layout is null");
        byte[] buffer = driver.getBuffer();
        for (byte d : buffer)
            bufferLayout.processByte(d);
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
