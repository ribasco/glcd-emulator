package com.ibasco.glcdemu;

@SuppressWarnings({"Duplicates", "WeakerAccess"})
abstract public class GlcdInstruction {

    //Display Instruction Flags
    public static final int F_DISPLAY_CLEAR = 0x1;
    public static final int F_HOME = 0x2;
    public static final int F_ENTRY_MODE_SET = 0x4;
    public static final int F_DISPLAY_CONTROL = 0x8;
    public static final int F_DISPLAY_CURSOR_CONTROL = 0x10;
    public static final int F_FUNCTION_SET = 0x20;
    public static final int F_CGRAM_SET = 0x40;
    public static final int F_DDRAM_SET = 0x80;

    private byte value;

    private int flag;

    private String name;

    public GlcdInstruction(int flag, String name, byte value) {
        this.value = value;
        this.name = name;
        this.flag = flag;
    }

    public byte getValue() {
        return value;
    }

    public int getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        /*final StringBuffer sb = new StringBuffer("GlcdInstruction{");
        sb.append("value=").append(ByteUtils.toHexString(false, value));
        sb.append(", flag=").append(ByteUtils.toHexString(false, (byte) flag));
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();*/
        return "";
    }
}
