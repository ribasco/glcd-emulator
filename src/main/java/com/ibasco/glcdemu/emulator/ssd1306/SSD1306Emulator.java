/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: SSD1306Emulator.java
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
package com.ibasco.glcdemu.emulator.ssd1306;

import com.ibasco.glcdemu.annotations.Emulator;
import com.ibasco.glcdemu.emulator.GlcdEmulatorBase;
import com.ibasco.ucgdisplay.core.u8g2.U8g2ByteEvent;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;

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
    public void onByteEvent(U8g2ByteEvent event) {

    }

    @Override
    public void reset() {

    }
}
