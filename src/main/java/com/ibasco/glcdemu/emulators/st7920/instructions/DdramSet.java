package com.ibasco.glcdemu.emulators.st7920.instructions;

import com.ibasco.glcdemu.emulators.st7920.ST7920Instruction;
import com.ibasco.glcdemu.emulators.st7920.ST7920InstructionFlags;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class DdramSet extends ST7920Instruction {

    private static final Logger log = getLogger(DdramSet.class);

    public static final int ADDRESS_X = 0xF;

    public static final int ADDRESS_Y = 0x3F;

    private int addressType;

    public DdramSet(byte value, int addressType) {
        super(ST7920InstructionFlags.DDRAM_SET, value);
        this.addressType = addressType;
    }

    public int getAddressType() {
        return addressType;
    }

    public int getAddress() {
        return getValue() & addressType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SET ");
        sb.append((addressType == ADDRESS_X) ? "X" : "Y")
                .append(" = ")
                .append(getAddress())
                .append(", VALUE = ")
                .append(getValue());
        return sb.toString();
    }
}
