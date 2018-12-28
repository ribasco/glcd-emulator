/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: DdramSet.java
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
package com.ibasco.glcdemulator.emulator.st7920.instructions;

import com.ibasco.glcdemulator.emulator.st7920.ST7920Instruction;
import com.ibasco.glcdemulator.emulator.st7920.ST7920InstructionFlag;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class DdramSet extends ST7920Instruction {

    private static final Logger log = getLogger(DdramSet.class);

    public static final int ADDRESS_X = 0xF;

    public static final int ADDRESS_Y = 0x3F;

    private int addressType;

    public DdramSet(byte value, int addressType) {
        super(ST7920InstructionFlag.DDRAM_SET, value);
        this.addressType = addressType;
    }

    public int getAddressType() {
        return addressType;
    }

    public int getAddress() {
        return getValue() & addressType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SET ");
        sb.append((addressType == ADDRESS_X) ? "X" : "Y")
                .append(" = ")
                .append(getAddress())
                .append(", VALUE = ")
                .append(getValue());
        return sb.toString();
    }
}
