package com.ibasco.glcdemu.emulator.comm;


import com.ibasco.glcdemu.GlcdCommDecoder;
import com.ibasco.glcdemu.GlcdCommEncoder;
import com.ibasco.pidisplay.core.exceptions.NotImplementedException;
import com.ibasco.pidisplay.core.u8g2.U8g2GpioEvent;
import com.ibasco.pidisplay.core.u8g2.U8g2Message;

import java.util.Objects;

public class GlcdSpiProtocolAdapter implements GlcdCommDecoder, GlcdCommEncoder {

    private int _decode_bit_index = 7;
    private byte _decode_data = 0;

    @Override
    public Byte decode(U8g2GpioEvent event) {
        U8g2Message msg = Objects.requireNonNull(event, "Gpio event cannot be null").getMessage();
        switch (msg) {
            case U8X8_MSG_BYTE_INIT:
                break;
            case U8X8_MSG_BYTE_START_TRANSFER:
                break;
            case U8X8_MSG_BYTE_END_TRANSFER:
                break;
            // Marks the start of the byte transfer operation
            case U8X8_MSG_BYTE_SEND:
                _decode_bit_index = 7; //set the starting bit index
                break;
            case U8X8_MSG_GPIO_D0: //spi-clock
                //for every clock tick, check if we have received a whole byte
                if (event.getValue() == 1) {
                    if (_decode_bit_index < 0) {
                        try {
                            return _decode_data;
                        } finally {
                            _decode_data = 0;
                        }
                    }
                    _decode_bit_index &= 0x7;
                }
                break;
            // The incoming _decode_data (in bits) will be collected and re-assembled into a byte
            case U8X8_MSG_GPIO_D1: //spi-_decode_data
                _decode_data ^= (-(event.getValue()) ^ _decode_data) & (1 << _decode_bit_index--);
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public U8g2GpioEvent encode(byte data) {
        throw new NotImplementedException("Encode functionality not yet implemented");
    }
}
