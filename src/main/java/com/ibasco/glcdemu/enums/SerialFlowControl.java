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
