package com.ibasco.glcdemu.enums;

import com.ibasco.glcdemu.utils.EnumValue;

import java.util.Arrays;

import static com.fazecast.jSerialComm.SerialPort.*;

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
