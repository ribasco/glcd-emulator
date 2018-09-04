package com.ibasco.glcdemu.emulators;

import com.ibasco.pidisplay.core.util.ByteUtils;

abstract public class GlcdInstruction<T extends GlcdInstructionFlag> {

    private byte value;

    private T flag;

    public GlcdInstruction(T flag, byte value) {
        this.value = value;
        this.flag = flag;
    }

    public byte getValue() {
        return value;
    }

    public T getFlag() {
        return flag;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GlcdInstruction{");
        sb.append("value=").append(ByteUtils.toHexString(false, value));
        sb.append(", flag=").append(ByteUtils.toHexString(false, (byte) flag.getCode()));
        sb.append('}');
        return sb.toString();
    }
}
