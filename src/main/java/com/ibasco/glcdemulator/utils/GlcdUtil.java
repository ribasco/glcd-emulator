/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdUtil.java
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
package com.ibasco.glcdemulator.utils;

import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.emulator.GlcdEmulator;
import com.ibasco.ucgdisplay.drivers.glcd.Glcd;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdSetupInfo;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdSize;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlcdUtil {

    private static final Logger log = LoggerFactory.getLogger(GlcdUtil.class);

    private static final Map<GlcdBusInterface, Integer> busPriority = new HashMap<>();

    static {
        busPriority.put(GlcdBusInterface.PARALLEL_8080, 1);
        busPriority.put(GlcdBusInterface.PARALLEL_6800, 2);
        busPriority.put(GlcdBusInterface.SPI_HW_4WIRE, 3);
        busPriority.put(GlcdBusInterface.SPI_SW_4WIRE, 4);
        busPriority.put(GlcdBusInterface.SPI_SW_3WIRE, 5);
        busPriority.put(GlcdBusInterface.I2C_HW, 6);
        busPriority.put(GlcdBusInterface.I2C_SW, 7);
        busPriority.put(GlcdBusInterface.PARALLEL_6800_KS0108, 8);
        busPriority.put(GlcdBusInterface.SPI_HW_4WIRE_ST7920, 9);
        busPriority.put(GlcdBusInterface.SPI_SW_4WIRE_ST7920, 10);
        busPriority.put(GlcdBusInterface.SERIAL_HW, 11);
        busPriority.put(GlcdBusInterface.SERIAL_SW, 12);
        busPriority.put(GlcdBusInterface.SED1520, 99);
    }

    public static Predicate<GlcdDisplay> bySize(GlcdSize size) {
        return p -> p.getDisplaySize().equals(size);
    }

    public static String findSetupFunction(GlcdDisplay display) {
        return findSetupFunction(display, null);
    }

    public static String findSetupFunction(GlcdDisplay display, GlcdBusInterface busInterface) {
        if (display == null)
            throw new IllegalArgumentException("Display cannot be null");
        final GlcdBusInterface tmp = busInterface == null ? findPreferredBusInterface(display) : busInterface;
        GlcdSetupInfo setupInfo = Arrays.stream(display.getSetupDetails())
                .filter(setup -> setup.isSupported(tmp))
                .findFirst()
                .orElse(null);
        return setupInfo != null ? setupInfo.getFunction() : null;
    }

    public static GlcdBusInterface findPreferredBusInterface(GlcdDisplay display) {
        List<GlcdBusInterface> tmp = new ArrayList<>(display.getBusInterfaces());
        tmp.sort(Comparator.comparing(busPriority::get));
        return tmp.get(0);
    }

    public static List<GlcdBusInterface> findSupportedBusInterface(Class<? extends GlcdEmulator> emulator) {
        return Arrays.asList(emulator.getAnnotation(Emulator.class).bus());
    }

    public static List<GlcdDisplay> findDisplayByType(GlcdControllerType type, Predicate<GlcdDisplay> filter) {
        Predicate<GlcdDisplay> findByType = p -> p.getController().equals(type);
        return getDisplayList().stream().filter(findByType.and(filter)).collect(Collectors.toList());
    }

    public static List<GlcdDisplay> findDisplay(Predicate<GlcdDisplay> filter) {
        return getDisplayList().stream().filter(filter).collect(Collectors.toList());
    }

    @SuppressWarnings("Duplicates")
    public static Class<? extends GlcdEmulator> findEmulatorClass(GlcdDisplay display) {
        if (display == null)
            throw new IllegalArgumentException("Display argument cannot be null");
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages("com.ibasco").scan()) {
            ClassInfoList classInfo = scanResult.getClassesImplementing(GlcdEmulator.class.getName()).filter(f -> !f.isAbstract() && f.hasAnnotation(Emulator.class.getName()));
            List<Class<GlcdEmulator>> result = classInfo.loadClasses(GlcdEmulator.class);
            if (!result.isEmpty()) {
                for (Class<GlcdEmulator> emulatorClass : result) {
                    GlcdControllerType type = emulatorClass.getAnnotation(Emulator.class).controller();
                    if (type.equals(display.getController()))
                        return emulatorClass;
                }
            }
        }
        return null;
    }

    /**
     * @return A list of all available {@link GlcdDisplay}
     */
    public static List<GlcdDisplay> getDisplayList() {
        List<GlcdDisplay> displayList = new ArrayList<>();
        Class<?>[] controllers = Glcd.class.getDeclaredClasses();
        for (Class<?> controllerClass : controllers) {
            displayList.addAll(
                    Arrays
                            .stream(controllerClass.getDeclaredFields())
                            .map(GlcdUtil::convert)
                            .collect(Collectors.toList())
            );
        }
        return displayList;
    }

    private static GlcdDisplay convert(Field field) {
        try {
            return (GlcdDisplay) field.get(null);
        } catch (IllegalAccessException e) {
            log.warn("Could not convert field to GlcdDisplay", e);
        }
        return null;
    }
}
