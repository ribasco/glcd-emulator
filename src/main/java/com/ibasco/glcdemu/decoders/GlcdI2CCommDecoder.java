package com.ibasco.glcdemu.decoders;

import com.ibasco.glcdemu.GlcdCommDecoder;
import com.ibasco.glcdemu.GpioActivity;

public class GlcdI2CCommDecoder extends GlcdCommDecoder {

    public GlcdI2CCommDecoder(GlcdCommDecoder.ByteEventHandler byteEventHandler) {
        super(byteEventHandler);
    }

    @Override
    public void decode(GpioActivity event) {

    }
}
