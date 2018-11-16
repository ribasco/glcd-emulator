/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdEmulatorFactory.java
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

import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.exceptions.EmulatorFactoryException;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class GlcdEmulatorFactory {

    private static final Logger log = LoggerFactory.getLogger(GlcdEmulatorFactory.class);

    public static GlcdEmulator createFrom(GlcdDisplay display, GlcdBusInterface busInterface) {
        return createFrom(display, busInterface, null);
    }

    public static GlcdEmulator createFrom(GlcdDisplay display, GlcdBusInterface busInterface, PixelBuffer buffer) {
        if (display == null)
            throw new IllegalArgumentException("Display cannot be null");

        GlcdEmulator emulator = createFrom(display.getController());

        if (emulator == null)
            throw new IllegalStateException("Emulator not found for display: " + display);

        if (busInterface == null) {
            busInterface = emulator.getClass().getAnnotation(Emulator.class).defaultBus();
            log.debug("No bus interface specified. Using default = {}", busInterface);
        }

        if (buffer == null) {
            buffer = new PixelBuffer(
                    display.getDisplaySize().getDisplayWidth(),
                    display.getDisplaySize().getDisplayHeight()
            );
        }
        emulator.setBusInterface(busInterface);
        emulator.setBuffer(buffer);
        return emulator;
    }

    /**
     * Scans for classes in the classpath which match against the provided {@link GlcdControllerType}
     *
     * @param type
     *         The {@link GlcdControllerType} to scan
     *
     * @return An instance of {@link GlcdEmulator} or null if nothing has been found
     */
    public static GlcdEmulator createFrom(GlcdControllerType type) {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(Context.class.getPackage().getName()).scan()) {
            ClassInfoList classInfo = scanResult
                    .getClassesImplementing(GlcdEmulator.class.getName())
                    .filter(f -> !f.isAbstract() && f.hasAnnotation(Emulator.class.getName()));
            List<Class<GlcdEmulator>> result = classInfo.loadClasses(GlcdEmulator.class);
            if (result.size() > 0) {
                for (Class<GlcdEmulator> emulatorClass : result) {
                    GlcdControllerType cType = emulatorClass.getAnnotation(Emulator.class).controller();
                    if (cType.equals(type))
                        return createFrom(emulatorClass);
                }
            }
        }
        return null;
    }

    public static GlcdEmulator createFrom(Class<? extends GlcdEmulator> emulatorClass) {
        try {
            return emulatorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EmulatorFactoryException("Unable to instantiate emulator '" + emulatorClass + "'", e);
        }
    }
}
