/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdBusEncoder.java
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
package com.ibasco.glcdemu.emulator;

import com.ibasco.ucgdisplay.core.u8g2.U8g2GpioEvent;

/**
 * Converts bytes to {@link U8g2GpioEvent}
 *
 * @author Rafael Ibasco
 */
public interface GlcdBusEncoder {
    U8g2GpioEvent encode(byte data);
}
