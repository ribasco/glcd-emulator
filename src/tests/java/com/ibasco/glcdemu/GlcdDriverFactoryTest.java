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
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

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