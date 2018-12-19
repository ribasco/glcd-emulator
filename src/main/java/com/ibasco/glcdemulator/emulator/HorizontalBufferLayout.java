/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: HorizontalBufferLayout.java
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
package com.ibasco.glcdemulator.emulator;

import com.ibasco.glcdemulator.utils.ByteUtils;
import com.ibasco.glcdemulator.utils.PixelBuffer;

public class HorizontalBufferLayout extends BufferLayout {
    @Override
    public void processByte(byte data) {
        PixelBuffer buffer = getBuffer();
        buffer.write(ByteUtils.reverse(data));
        if (buffer.remaining() == 0) {
            buffer.reset();
        }
    }

    @Override
    public void reset() {
        getBuffer().reset();
    }

    @Override
    public void initialize() {

    }
}