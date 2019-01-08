/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: BufferLayoutFactory.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 - 2019 Rafael Luis Ibasco
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
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBufferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Rafael Ibasco
 */
public class BufferLayoutFactory {
    public static final Logger log = LoggerFactory.getLogger(BufferLayoutFactory.class);

    public static BufferLayout createBufferLayout(Class<? extends GlcdEmulator> emulatorClass) {
        GlcdBufferType bufferStrategy = emulatorClass.getAnnotation(Emulator.class).bufferLayout();
        return createBufferLayout(bufferStrategy);
    }

    public static BufferLayout createBufferLayout(GlcdBufferType type) {
        Class<? extends BufferLayout> clsBufferStrategy = retrieveLayoutClass(type);
        BufferLayout bufferStrategyInstance;
        try {
            bufferStrategyInstance = clsBufferStrategy.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new BufferStrategyFactoryException(e);
        }
        return bufferStrategyInstance;
    }

    public static BufferLayout createBufferLayout(GlcdDisplay display) {
        return createBufferLayout(display, null);
    }

    public static BufferLayout createBufferLayout(GlcdDisplay display, PixelBuffer buffer) {
        log.debug("Buffer type/layout = {}", display.getBufferType());
        Class<? extends BufferLayout> layoutClass = retrieveLayoutClass(display.getBufferType());
        BufferLayout bLayout = createInstance(layoutClass);
        bLayout.setBuffer(buffer);
        return bLayout;
    }

    private static Class<? extends BufferLayout> retrieveLayoutClass(GlcdBufferType type) {
        Class<? extends BufferLayout> layoutClass = null;
        switch (type) {
            case HORIZONTAL:
                layoutClass = HorizontalBufferLayout.class;
                break;
            case VERTICAL:
                layoutClass = VerticalBufferLayout.class;
                break;
        }
        return layoutClass;
    }

    private static BufferLayout createInstance(Class<? extends BufferLayout> cls) {
        try {
            return cls.getConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new BufferLayoutLoadException("Unable to create instance for buffer layout class: " + cls, e);
        }
    }
}
