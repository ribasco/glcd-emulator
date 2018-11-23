package com.ibasco.glcdemulator.emulator.ssd1306;

import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.emulator.GlcdBufferStrategy;
import com.ibasco.glcdemulator.emulator.PagedBufferEmulator;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;

@Emulator(
        controller = GlcdControllerType.SSD1306,
        description = "Emulator for SSD1306 controller",
        bus = {
                GlcdBusInterface.SPI_SW_4WIRE,
                GlcdBusInterface.SPI_HW_4WIRE,
                GlcdBusInterface.PARALLEL_6800,
                GlcdBusInterface.PARALLEL_8080
        },
        defaultBus = GlcdBusInterface.PARALLEL_8080,
        bufferStrategy = GlcdBufferStrategy.PAGED_BUFFERING
)
public class SSD1306Emulator extends PagedBufferEmulator {

}
