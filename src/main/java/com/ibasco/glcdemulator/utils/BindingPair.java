/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: BindingPair.java
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
package com.ibasco.glcdemulator.utils;

import javafx.beans.value.ObservableValue;

import java.util.Objects;

abstract class BindingPair<T> {
    ObservableValue<? extends T> value1;
    ObservableValue<? extends T> value2;

    BindingPair(ObservableValue<? extends T> value1, ObservableValue<? extends T> value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    abstract void bind();

    abstract void unbind();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindingPair that = (BindingPair) o;
        return Objects.equals(value1, that.value1) &&
                Objects.equals(value2, that.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value1, value2);
    }

    @Override
    public String toString() {
        return "BindingPair{" +
                "value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }
}
