/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: BufferLayoutFactory.java
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
package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.exceptions.BufferLayoutLoadException;
import com.ibasco.glcdemulator.exceptions.BufferStrategyFactoryException;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author Rafael Ibasco
 */
public class BufferLayoutFactory {
    public static final Logger log = LoggerFactory.getLogger(BufferLayoutFactory.class);

    private static final String LAYOUT_FILE = "bufferLayout.properties";

    private static Properties layoutProperties;

    public static BufferLayout createBufferLayout(Class<? extends GlcdEmulator> emulatorClass) {
        GlcdBufferLayout bufferStrategy = emulatorClass.getAnnotation(Emulator.class).bufferLayout();
        return createBufferLayout(bufferStrategy);
    }

    public static BufferLayout createBufferLayout(GlcdBufferLayout layout) {
        Class<? extends BufferLayout> clsBufferStrategy = layout.getLayoutClass();
        BufferLayout bufferStrategyInstance;
        try {
            bufferStrategyInstance = clsBufferStrategy.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BufferStrategyFactoryException(e);
        }
        return bufferStrategyInstance;
    }

    public static BufferLayout createBufferLayout(GlcdDisplay display, PixelBuffer buffer) {
        log.debug("Buffer layout display = {}", display.getController().name());
        Properties layoutMapping = getLayoutProperties();
        GlcdBufferLayout layout;
        if (layoutMapping == null) {
            layout = GlcdBufferLayout.VERTICAL;
        } else {
            String layoutName = layoutMapping.getProperty(display.getController().name());
            if (StringUtils.isBlank(layoutName)) {
                log.warn("No buffer layout mapping found for display controller '{}'. Using default.", display.getController().name());
                layout = GlcdBufferLayout.VERTICAL;
            } else {
                layout = GlcdBufferLayout.valueOf(layoutName.toUpperCase());
            }
        }
        BufferLayout bLayout = createInstance(layout.getLayoutClass());
        bLayout.setBuffer(buffer);
        return bLayout;
    }

    private static BufferLayout createInstance(Class<? extends BufferLayout> cls) {
        try {
            return cls.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new BufferLayoutLoadException("Unable to create instance for buffer layout class: " + cls, e);
        }
    }

    private static Properties getLayoutProperties() {
        if (layoutProperties == null) {
            layoutProperties = new Properties();
            try {
                layoutProperties.load(BufferLayoutFactory.class.getClassLoader().getResourceAsStream(LAYOUT_FILE));
            } catch (IOException e) {
                throw new BufferLayoutLoadException("Unable to load layout mapping file from classpath", e);
            }
        }
        return layoutProperties;
    }
}
