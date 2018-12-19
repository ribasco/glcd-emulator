/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: SSD1306Emulator.java
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
package com.ibasco.glcdemulator.emulator.ssd1306;

import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.emulator.GlcdBufferLayout;
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
        bufferLayout = GlcdBufferLayout.VERTICAL
)
public class SSD1306Emulator extends PagedBufferEmulator {

}
