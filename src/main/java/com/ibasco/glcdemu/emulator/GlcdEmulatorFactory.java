package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.annotations.Emulator;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.pidisplay.core.util.ByteUtils;
import com.ibasco.pidisplay.drivers.glcd.*;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdCommInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdFont;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdRotation;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GlcdEmulatorFactory {

    private static final Logger log = LoggerFactory.getLogger(GlcdEmulatorFactory.class);

    /**
     * Scans for classes with the given {@link GlcdControllerType}
     *
     * @param type
     *         The {@link GlcdControllerType} to scan on {@link GlcdEmulator} classes
     *
     * @return An instance of {@link GlcdEmulator} or null if nothing has been found
     */
    public static GlcdEmulator createEmulatorFromType(GlcdControllerType type) {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(Context.class.getPackage().getName()).scan()) {
            ClassInfoList classInfo = scanResult
                    .getClassesImplementing(GlcdEmulator.class.getName())
                    .filter(f -> !f.isAbstract() && f.hasAnnotation(Emulator.class.getName()));
            List<Class<GlcdEmulator>> result = classInfo.loadClasses(GlcdEmulator.class);
            if (result.size() > 0) {
                for (Class<GlcdEmulator> emulatorClass : result) {
                    GlcdControllerType cType = emulatorClass.getAnnotation(Emulator.class).controller();
                    if (cType.equals(type))
                        return createEmulatorFromClass(emulatorClass);
                }
            }
        }
        return null;
    }

    public static GlcdEmulator createEmulatorFromClass(Class<? extends GlcdEmulator> emulatorClass) {
        try {
            return emulatorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate emulator controller '" + emulatorClass + "'", e);
        }
    }

    public static GlcdDriver createDriver(GlcdDisplay display) {
        GlcdEmulator emulator = createEmulatorFromType(display.getController());

        if (emulator == null)
            throw new IllegalStateException("No emulator found for display '" + display.getName() + "'");

        GlcdConfig config = GlcdConfigBuilder
                .create()
                .display(display)
                .rotation(GlcdRotation.ROTATION_NONE)
                .commInterface(GlcdCommInterface.PARALLEL_6800)
                .build();
        config.setEmulated(true);

        return new GlcdDriver(config) {

            private int ctr = 0;

            @Override
            protected void onByteEvent(U8g2ByteEvent event) {
                switch (event.getMessage()) {
                    case U8X8_MSG_BYTE_SEND:
                        int value = event.getValue();
                        log.debug("{}) Byte: {}", ++ctr, ByteUtils.toHexString(false, (byte) value));
                        break;
                    case U8X8_MSG_BYTE_INIT:
                        log.debug("BYTE INIT");
                        break;
                    case U8X8_MSG_BYTE_START_TRANSFER:
                        log.debug("START");
                        break;
                    case U8X8_MSG_BYTE_END_TRANSFER:
                        log.debug("END");
                        break;
                }
            }
        };
    }

    public static void main(String[] args) {
        GlcdDriver driver = createDriver(Glcd.ST7920.D_128x64);
        driver.clearBuffer();
        driver.setFont(GlcdFont.FONT_OPEN_ICONIC_EMAIL_1X_T);
        driver.drawString(10, 10, "Hello");
        driver.sendBuffer();
    }
}
