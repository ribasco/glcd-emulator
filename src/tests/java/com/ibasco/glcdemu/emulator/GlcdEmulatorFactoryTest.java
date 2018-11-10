/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdEmulatorFactoryTest.java
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
package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.emulator.st7920.ST7920Emulator;
import com.ibasco.ucgdisplay.drivers.glcd.Glcd;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import static junit.framework.TestCase.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
