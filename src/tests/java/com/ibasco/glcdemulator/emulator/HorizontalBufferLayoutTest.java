/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: HorizontalBufferLayoutTest.java
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

import com.ibasco.glcdemulator.utils.PixelBuffer;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class HorizontalBufferLayoutTest {

    public static final Logger log = LoggerFactory.getLogger(HorizontalBufferLayoutTest.class);

    private HorizontalBufferLayout layout;

    private PixelBuffer buffer;
    /*@Mock
    private PixelBuffer buffer;*/

    @BeforeEach
    void setUp() {
        layout = new HorizontalBufferLayout();
        buffer = new PixelBuffer(128, 64);
        layout.setBuffer(buffer);
    }

    @Test
    void processByte() {
        byte[] data = RandomUtils.nextBytes(2048);
        log.info("Size = {}", buffer.size());
        for (byte d : data) {
            log.info("pos: {}, remaining = {}, x = {}, y = {}", buffer.position(), buffer.remaining(), buffer.xPosition(), buffer.yPosition());
            layout.processByte(d);
            if (buffer.remaining() == 0) {
                log.info("resetting buffer");
                buffer.reset();
            }
        }
    }
}
