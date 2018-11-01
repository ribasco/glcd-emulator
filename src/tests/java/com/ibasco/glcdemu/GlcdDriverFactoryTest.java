/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: GlcdDriverFactoryTest.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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
package com.ibasco.glcdemu;

import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.emulator.GlcdEmulatorFactory;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.ucgdisplay.common.utils.NativeLibraryLoader;
import com.ibasco.ucgdisplay.core.u8g2.U8g2Graphics;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDriver;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.Mock;
import static org.powermock.api.mockito.PowerMockito.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.junit.jupiter.api.Test;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GlcdDriverFactory.class, GlcdEmulatorFactory.class, GlcdDriver.class, U8g2Graphics.class, NativeLibraryLoader.class})
class GlcdDriverFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(GlcdDriverFactoryTest.class);

    @Mock
    GlcdEmulator mockedEmulator;

    @Mock
    PixelBuffer mockedBuffer;

    public GlcdDriverFactoryTest() {
    }

    @Test
    @DisplayName("Create driver by controller type")
    public void createVirtualByType() throws Exception {
        mockStatic(GlcdDriver.class);
        mockStatic(GlcdEmulatorFactory.class);
        mockStatic(U8g2Graphics.class);
        mockStatic(NativeLibraryLoader.class);

        suppress(methodsDeclaredIn(NativeLibraryLoader.class));

        when(mockedBuffer.getWidth()).thenReturn(128);
        when(mockedBuffer.getHeight()).thenReturn(64);
        when(GlcdEmulatorFactory.createFrom(any(GlcdDisplay.class), eq(GlcdBusInterface.PARALLEL_8080), eq(mockedBuffer))).thenReturn(mockedEmulator);
        when(U8g2Graphics.setup(anyString(), anyInt(), anyInt(), anyInt(), anyInt(), any(), eq(true))).thenReturn(1L);

        GlcdDriver driver = GlcdDriverFactory.createVirtual(GlcdControllerType.ST7920, GlcdBusInterface.PARALLEL_8080, mockedBuffer);

        assertNotNull(driver);
        assertNotNull(driver.getDriverEventHandler());
        assertSame(driver.getDriverEventHandler(), mockedEmulator);
        assertEquals(1L, driver.getId());
        assertEquals("u8g2_Setup_st7920_p_128x64_f", driver.getConfig().getSetupProcedure());
    }
}
