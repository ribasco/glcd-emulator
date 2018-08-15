package com.ibasco.glcdemu;

/**
 * Communications Protocol Decoder
 *
 * @author Rafael Ibasco
 */
@SuppressWarnings("unused")
abstract public class GlcdCommDecoder {

    /**
     * U8G2 message flags
     */
    protected static final int U8X8_MSG_GPIO_AND_DELAY_INIT = 0x28;
    protected static final int U8X8_MSG_GPIO_D0 = 0x40;
    protected static final int U8X8_MSG_GPIO_D1 = 0x41;
    protected static final int U8X8_MSG_GPIO_D2 = 0x42;
    protected static final int U8X8_MSG_GPIO_D3 = 0x43;
    protected static final int U8X8_MSG_GPIO_D4 = 0x44;
    protected static final int U8X8_MSG_GPIO_D5 = 0x45;
    protected static final int U8X8_MSG_GPIO_D6 = 0x46;
    protected static final int U8X8_MSG_GPIO_D7 = 0x47;
    protected static final int U8X8_MSG_GPIO_CS = 0x49;
    protected static final int U8X8_MSG_GPIO_E = 0x48;
    protected static final int U8X8_MSG_GPIO_CS1 = 0x4e;
    protected static final int U8X8_MSG_GPIO_CS2 = 0x4f;
    protected static final int U8X8_MSG_GPIO_RESET = 0x4b;
    protected static final int U8X8_MSG_GPIO_DC = 0x4a;

    protected static final int U8X8_MSG_BYTE_INIT = 0x14;
    protected static final int U8X8_MSG_DELAY_MILLI = 0x29;
    protected static final int U8X8_MSG_DELAY_NANO = 0x2c;
    protected static final int U8X8_MSG_BYTE_SEND = 0x17;
    protected static final int U8X8_MSG_BYTE_START_TRANSFER = 0x18;
    protected static final int U8X8_MSG_BYTE_END_TRANSFER = 0x19;
    protected static final int U8X8_MSG_BYTE_SET_DC = 0x20;
    protected static final int U8X8_MSG_GPIO_I2C_DATA = 0x4d;
    protected static final int U8X8_MSG_GPIO_I2C_CLOCK = 0x4c;
    protected static final int U8X8_MSG_DELAY_I2C = 0x2d;

    private ByteEventHandler byteEventHandler;

    @FunctionalInterface
    public interface ByteEventHandler {
        void onByteEvent(int data);
    }

    public GlcdCommDecoder(ByteEventHandler byteEventHandler) {
        this.byteEventHandler = byteEventHandler;
    }

    protected void emitByteEvent(byte data) {
        if (byteEventHandler != null)
            byteEventHandler.onByteEvent(Byte.toUnsignedInt(data));
    }

    abstract public void decode(GpioActivity event);
}
