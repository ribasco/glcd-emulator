/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: PagedBufferEmulator.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Luis Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
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
                    getBufferLayout().processByte((byte) event.getValue());
                }
                //note: command instructions are ignored
                break;
            default:
                break;
        }
    }
}
