/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: SerialStopBits.java
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

import com.fazecast.jSerialComm.SerialPort;
import com.ibasco.glcdemulator.utils.EnumValue;

import java.util.Arrays;

public enum SerialStopBits implements EnumValue<Integer> {
    ONE_STOP_BIT(SerialPort.ONE_STOP_BIT),
    ONE_POINT_FIVE_STOP_BITS(SerialPort.ONE_POINT_FIVE_STOP_BITS),
    TWO_STOP_BITS(SerialPort.TWO_STOP_BITS);

    private int value;

    SerialStopBits(int stopBit) {
        this.value = stopBit;
    }

    @Override
    public Integer toValue() {
        return value;
    }

    public static SerialStopBits fromValue(int value) {
        return Arrays.stream(values()).filter(p -> p.toValue() == value).findFirst().orElse(null);
    }
}
