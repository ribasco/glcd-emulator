package com.ibasco.glcdemu.emulator;

import com.ibasco.glcdemu.utils.ByteUtils;

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
        sb.append("value=").append(ByteUtils.toHexString(value));
        sb.append(", flag=").append(ByteUtils.toHexString((byte) flag.getCode()));
        sb.append('}');
        return sb.toString();
    }
}
