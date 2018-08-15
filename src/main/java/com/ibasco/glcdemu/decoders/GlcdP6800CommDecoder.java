package com.ibasco.glcdemu.decoders;

import com.ibasco.glcdemu.GlcdCommDecoder;
import com.ibasco.glcdemu.GpioActivity;

public class GlcdP6800CommDecoder extends GlcdCommDecoder {

    public GlcdP6800CommDecoder(ByteEventHandler byteEventHandler) {
        super(byteEventHandler);
    }

    @Override
    public void decode(GpioActivity event) {

    }
}
