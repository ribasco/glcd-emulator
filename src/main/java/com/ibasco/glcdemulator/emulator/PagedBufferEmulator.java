package com.ibasco.glcdemulator.emulator;

import com.ibasco.ucgdisplay.core.u8g2.U8g2ByteEvent;

abstract public class PagedBufferEmulator extends GlcdEmulatorBase {
    private GlcdRegisterSelect dataCommand;

    @Override
    public void onByteEvent(U8g2ByteEvent event) {
        switch (event.getMessage()) {
            case U8X8_MSG_BYTE_SET_DC:
                dataCommand = event.getValue() == 0 ? GlcdRegisterSelect.COMMAND : GlcdRegisterSelect.DATA;
                break;
            case U8X8_MSG_BYTE_SEND:
                if (GlcdRegisterSelect.DATA.equals(dataCommand)) {
                    getBufferStrategy().processByte((byte) event.getValue());
                }
                //note: command instructions are ignored
                break;
            default:
                break;
        }
    }
}
