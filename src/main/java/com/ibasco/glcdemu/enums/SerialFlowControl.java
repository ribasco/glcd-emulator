/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: SerialFlowControl.java
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

import com.fazecast.jSerialComm.SerialPort;
import com.ibasco.glcdemu.utils.EnumValue;

import java.util.ArrayList;
import java.util.List;

public enum SerialFlowControl implements EnumValue<Integer> {
    NONE(SerialPort.FLOW_CONTROL_DISABLED),
    RTS(SerialPort.FLOW_CONTROL_RTS_ENABLED),
    CTS(SerialPort.FLOW_CONTROL_CTS_ENABLED),
    DSR(SerialPort.FLOW_CONTROL_DSR_ENABLED),
    DTR(SerialPort.FLOW_CONTROL_DTR_ENABLED),
    XONXOFF_IN(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED),
    XONXOFF_OUT(SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED);

    private int value;

    SerialFlowControl(int value) {
        this.value = value;
    }

    public SerialFlowControl or(SerialFlowControl flowControl) {
        this.value |= flowControl.toValue();
        return this;
    }

    @Override
    public Integer toValue() {
        return value;
    }

    public boolean isSet(int value) {
        return (this.value & value) != 0;
    }

    public static List<SerialFlowControl> fromValue(int value) {
        List<SerialFlowControl> values = new ArrayList<>();
        for (SerialFlowControl fc : values()) {
            if ((fc.toValue() & value) != 0)
                values.add(fc);
        }
        return values;
    }
}
