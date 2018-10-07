package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.GlcdDriverFactory;
import com.ibasco.glcdemu.annotations.Emulator;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.pidisplay.drivers.glcd.Glcd;
import com.ibasco.pidisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.pidisplay.drivers.glcd.GlcdDriver;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdFont;
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
            throw new NullPointerException("Display cannot be null");

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
            throw new RuntimeException("Unable to instantiate emulator '" + emulatorClass + "'", e);
        }
    }

    public static void main(String[] args) {
        GlcdDisplay display = Glcd.ST7920.D_128x64;
        GlcdBusInterface busInterface = GlcdBusInterface.SPI_HW_4WIRE_ST7920;

        GlcdDriver driver = GlcdDriverFactory.createVirtual(display, busInterface);

        driver.clearBuffer();
        driver.setFont(GlcdFont.FONT_7X13_TR);
        driver.drawString(10, 10, "Hello World");
        driver.sendBuffer();
    }
}
