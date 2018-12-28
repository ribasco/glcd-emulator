/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: ST7920Instruction.java
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
package com.ibasco.glcdemulator.emulator.st7920;

import com.ibasco.glcdemulator.emulator.GlcdInstruction;

/**
 * Base class for ST7920 instruction
 *
 * @author Rafael Ibasco
 */
public class ST7920Instruction extends GlcdInstruction<ST7920InstructionFlag> {
    public ST7920Instruction(ST7920InstructionFlag flag, byte value) {
        super(flag, value);
    }

}
