package com.ibasco.glcdemu.utils;

import com.ibasco.glcdemu.GlcdDriverFactory;
import com.ibasco.glcdemu.controls.GlcdScreen;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.pidisplay.core.ui.Font;
import com.ibasco.pidisplay.drivers.glcd.Glcd;
import com.ibasco.pidisplay.drivers.glcd.GlcdDriver;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontRenderer {

    private static final Logger log = LoggerFactory.getLogger(FontRenderer.class);

    private GlcdEmulator emulator;

    private GlcdDriver driver;

    public class FontInfo {
        private int ascent;
        private int descent;
        private int maxCharWidth;
        private int maxCharHeight;

        FontInfo(int ascent, int descent, int maxCharWidth, int maxCharHeight) {
            this.ascent = ascent;
            this.descent = descent;
            this.maxCharWidth = maxCharWidth;
            this.maxCharHeight = maxCharHeight;
        }

        public int getAscent() {
            return ascent;
        }

        public int getDescent() {
            return descent;
        }

        public int getMaxCharWidth() {
            return maxCharWidth;
        }

        public int getMaxCharHeight() {
            return maxCharHeight;
        }
    }

    private final Object mutext = new Object();

    private static class InstanceHolder {
        private static FontRenderer INSTANCE = new FontRenderer();
    }

    private FontRenderer() {
        /*GlcdConfig config = new GlcdConfig();
        config.setDisplay(Glcd.ST7920.D_128x64);
        config.setBusInterface(GlcdBusInterface.SPI_HW_4WIRE_ST7920);
        config.setRotation(GlcdRotation.ROTATION_NONE);*/

        //emulator = new ST7920Emulator();
        driver = GlcdDriverFactory.createVirtual(Glcd.ST7920.D_128x64, GlcdBusInterface.SPI_HW_4WIRE_ST7920);//new GlcdDriver(config, true, emulator);
        emulator = driver.getDriverEventHandler();
        driver.setFont(GlcdFont.FONT_7X13B_TR); //default font
    }

    public GlcdDriver getDriver() {
        return driver;
    }

    public synchronized void renderFont(GlcdScreen screen, Font font, String text) {
        synchronized (mutext) {
            try {
                emulator.setBuffer(screen.getBuffer());
                driver.clearBuffer();
                driver.setFont(font);
                int y = (driver.getHeight() / 2) + (driver.getAscent() / 2);
                driver.drawString(5, y, text);
                driver.sendBuffer();
            } finally {
                emulator.setBuffer(null);
                emulator.reset();
            }
        }
    }

    public FontInfo getFontInfo(Font font) {
        synchronized (mutext) {
            driver.setFont(font);
            return new FontInfo(driver.getAscent(), driver.getDescent(), driver.getMaxCharWidth(), driver.getMaxCharHeight());
        }
    }

    public static FontRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
