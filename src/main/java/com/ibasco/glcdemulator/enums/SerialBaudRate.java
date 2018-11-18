/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: SerialBaudRate.java
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
package com.ibasco.glcdemulator.enums;

import com.ibasco.glcdemulator.exceptions.SerialException;
import com.ibasco.glcdemulator.utils.EnumValue;

import java.util.Arrays;

public enum SerialBaudRate implements EnumValue<Integer> {
    RATE_110(110),
    RATE_300(300),
    RATE_600(600),
    RATE_1200(1200),
    RATE_2400(2400),
    RATE_4800(4800),
    RATE_9600(9600),
    RATE_14400(14400),
    RATE_19200(19200),
    RATE_38400(38400),
    RATE_57600(57600),
    RATE_115200(115200),
    RATE_128000(128000),
    RATE_250000(250000),
    RATE_256000(256000),
    RATE_500Kb(500000),
    RATE_1Mb(1000000),
    RATE_2Mb(2000000),
    RATE_CUSTOM(-1);

    private int value;

    SerialBaudRate(int value) {
        this.value = value;
    }

    public SerialBaudRate customValue(int value) {
        if (!this.equals(RATE_CUSTOM))
            throw new SerialException("You can only use this method if the enum is RATE_CUSTOM");
        this.value = value;
        return this;
    }

    public static SerialBaudRate fromValue(int value) {
        return Arrays.stream(values()).filter(p -> p.value == value).findFirst().orElse(SerialBaudRate.RATE_CUSTOM.customValue(value));
    }

    @Override
    public Integer toValue() {
        return value;
    }
}
