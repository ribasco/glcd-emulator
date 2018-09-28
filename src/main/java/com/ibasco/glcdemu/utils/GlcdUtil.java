package com.ibasco.glcdemu.utils;

import com.ibasco.glcdemu.annotations.Emulator;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.pidisplay.drivers.glcd.Glcd;
import com.ibasco.pidisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlcdUtil {

    private static final Logger log = LoggerFactory.getLogger(GlcdUtil.class);

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

    public static Class<? extends GlcdEmulator> findEmulatorClass(GlcdDisplay display) {
        if (display == null)
            throw new NullPointerException("Display argument cannot be null");
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
