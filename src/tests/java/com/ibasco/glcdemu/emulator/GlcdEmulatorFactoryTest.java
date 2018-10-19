package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.emulator.st7920.ST7920Emulator;
import com.ibasco.ucgdisplay.drivers.glcd.Glcd;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.jupiter.api.Assertions.*;

class GlcdEmulatorFactoryTest {
    @Test
    @DisplayName("Create a known emulator from controller type")
    void createKnownEmulatorFromType() {
        assertNotNull(GlcdEmulatorFactory.createFrom(GlcdControllerType.ST7920));
    }

    @Test
    @DisplayName("Create non-existent emulator from controller type")
    void createUnknownEmulatorFromType() {
        assertNull(GlcdEmulatorFactory.createFrom(GlcdControllerType.LS013B7DH03));
    }

    @Test
    @DisplayName("Create emulator from class type")
    void createFromClass() {
        assertNotNull(GlcdEmulatorFactory.createFrom(ST7920Emulator.class));
    }

    @Test
    @DisplayName("Create with null display argument")
    void createWithNullDisplay() {
        assertThrows(NullPointerException.class, () -> GlcdEmulatorFactory.createFrom(null, null));
    }

    @Test
    @DisplayName("Create emulator with null bus argument")
    void createWithNullBus() {
        GlcdEmulator emulator = GlcdEmulatorFactory.createFrom(Glcd.ST7920.D_128x64, null);
        assertNotNull(emulator);
        assertNotNull(emulator.getBuffer());
        assertEquals(GlcdBusInterface.PARALLEL_8080, emulator.getBusInterface());
    }
}