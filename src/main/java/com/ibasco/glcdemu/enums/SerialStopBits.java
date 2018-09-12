package com.ibasco.glcdemu.enums;

import com.fazecast.jSerialComm.SerialPort;
import com.ibasco.glcdemu.utils.EnumValue;

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
