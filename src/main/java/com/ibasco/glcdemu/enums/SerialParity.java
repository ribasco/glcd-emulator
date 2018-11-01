/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: SerialParity.java
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
package com.ibasco.glcdemu.enums;

import static com.fazecast.jSerialComm.SerialPort.*;
import com.ibasco.glcdemu.utils.EnumValue;

import java.util.Arrays;

public enum SerialParity implements EnumValue<Integer> {
    NONE(NO_PARITY),
    ODD(ODD_PARITY),
    EVEN(EVEN_PARITY),
    MARK(MARK_PARITY),
    SPACE(SPACE_PARITY);

    private final int value;

    SerialParity(int parity) {
        this.value = parity;
    }

    @Override
    public Integer toValue() {
        return value;
    }

    public static SerialParity fromValue(int value) {
        return Arrays.stream(SerialParity.values()).filter(p -> p.toValue() == value).findFirst().orElse(null);
    }
}
