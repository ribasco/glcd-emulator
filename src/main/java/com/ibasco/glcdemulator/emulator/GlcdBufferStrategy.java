/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdBufferStrategy.java
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

public enum GlcdBufferStrategy {
    PAGED_BUFFERING(PagedBufferingStrategy.class, "Paged"),
    SIMPLE_BUFFERING(SimpleBufferingStrategy.class, "Basic");

    private Class<? extends BufferStrategy> strategyClass;

    private String name;

    GlcdBufferStrategy(Class<? extends BufferStrategy> cls, String name) {
        this.strategyClass = cls;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Class<? extends BufferStrategy> getStrategyClass() {
        return strategyClass;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
