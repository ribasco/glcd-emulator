/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdBufferLayout.java
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

/**
 * Enumeration for Buffer layout types
 *
 * @author Rafael Luis Ibasco
 */
public enum GlcdBufferLayout {
    VERTICAL(VerticalBufferLayout.class, "Vertical"),
    HORIZONTAL(HorizontalBufferLayout.class, "Horizontal");

    private Class<? extends BufferLayout> layoutClass;

    private String name;

    GlcdBufferLayout(Class<? extends BufferLayout> cls, String name) {
        this.layoutClass = cls;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Class<? extends BufferLayout> getLayoutClass() {
        return layoutClass;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
