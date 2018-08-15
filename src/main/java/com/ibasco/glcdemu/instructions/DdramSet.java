package com.ibasco.glcdemu.instructions;

import com.ibasco.glcdemu.GlcdInstruction;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class DdramSet extends GlcdInstruction {

    private static final Logger log = getLogger(DdramSet.class);

    public static final int ADDRESS_X = 0xF;

    public static final int ADDRESS_Y = 0x3F;

    private int addressType;

    public DdramSet(byte value, int addressType) {
        super(0x80, "DDRAM Set", value);
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
