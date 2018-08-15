package com.ibasco.glcdemu.decoders;

import com.ibasco.glcdemu.GlcdCommDecoder;
import com.ibasco.glcdemu.GpioActivity;

public class GlcddP8080CommDecoder extends GlcdCommDecoder {

    public GlcddP8080CommDecoder(GlcdCommDecoder.ByteEventHandler byteEventHandler) {
        super(byteEventHandler);
    }

    @Override
    public void decode(GpioActivity event) {

    }
}
