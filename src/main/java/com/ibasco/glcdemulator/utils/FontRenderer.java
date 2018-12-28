/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: FontRenderer.java
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
package com.ibasco.glcdemulator.utils;

import com.ibasco.glcdemulator.DriverFactory;
import com.ibasco.glcdemulator.controls.GlcdScreen;
import com.ibasco.glcdemulator.emulator.GlcdEmulator;
import com.ibasco.ucgdisplay.drivers.glcd.Glcd;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriver;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontRenderer {

    private static final Logger log = LoggerFactory.getLogger(FontRenderer.class);

    private GlcdEmulator emulator;

    private final Object mutext = new Object();

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

    private static class InstanceHolder {
        private static FontRenderer INSTANCE = new FontRenderer();
    }

    private FontRenderer() {
        driver = DriverFactory.createVirtual(Glcd.ST7920.D_128x64, GlcdBusInterface.SPI_HW_4WIRE_ST7920);
        emulator = driver.getDriverEventHandler();
        driver.setFont(GlcdFont.FONT_7X13B_TR); //default font
    }

    public GlcdDriver getDriver() {
        return driver;
    }

    public synchronized void renderFont(GlcdScreen screen, GlcdFont font, String text) {
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

    public FontInfo getFontInfo(GlcdFont font) {
        synchronized (mutext) {
            driver.setFont(font);
            return new FontInfo(driver.getAscent(), driver.getDescent(), driver.getMaxCharWidth(), driver.getMaxCharHeight());
        }
    }

    public static FontRenderer getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
