package com.ibasco.glcdemu.emulator.ssd1306;

import com.ibasco.glcdemu.annotations.Emulator;
import com.ibasco.glcdemu.emulator.GlcdEmulatorBase;
import com.ibasco.pidisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;

@Emulator(
        controller = GlcdControllerType.SSD1306,
        description = "Emulator for SSD1306 controller",
        bus = {
                GlcdBusInterface.PARALLEL_6800,
                GlcdBusInterface.PARALLEL_8080,
                GlcdBusInterface.SPI_SW_4WIRE,
                GlcdBusInterface.I2C_SW
        },
        defaultBus = GlcdBusInterface.I2C_SW
)
public class SSD1306Emulator extends GlcdEmulatorBase {
    @Override
    public void reset() {

    }

    @Override
    public void onByteEvent(U8g2ByteEvent event) {

    }
}
