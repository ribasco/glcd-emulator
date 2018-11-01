/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: BindGroup.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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
package com.ibasco.glcdemu.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a group of properties which facilitates
 *
 * @author Rafael Ibasco
 */
public class BindGroup {

    private static final Logger log = LoggerFactory.getLogger(BindGroup.class);

    private Set<BindingPair> bindings = new HashSet<>();

    @SuppressWarnings("unchecked")
    private class BidirectionalBindingPair<T> extends BindingPair<T> {

        private BidirectionalBindingPair(ObservableValue<T> value1, ObservableValue<T> value2) {
            super(value1, value2);
        }

        @Override
        void bind() {
            Bindings.bindBidirectional((Property<T>) value1, (Property<T>) value2);
            //log.debug("Bind: value1 = {}, value2 = {}, type = Bidirectional", value1, value2);
        }

        @Override
        void unbind() {
            Bindings.unbindBidirectional(value1, value2);
            //log.debug("Unbind: value1 = {}, value2 = {}, type = Bidirectional", value1, value2);
        }
    }

    @SuppressWarnings("unchecked")
    private class UnidirectionalBindingPair<T> extends BindingPair<T> {

        private UnidirectionalBindingPair(ObservableValue<T> value1, ObservableValue<? extends T> value2) {
            super(value1, value2);
        }

        @Override
        void bind() {
            ((Property<T>) value1).bind(value2);
            //log.debug("Bind: value1 = {}, value2 = {}, type = Unidrectional", value1, value2);
        }

        @Override
        void unbind() {
            ((Property<T>) value1).unbind();
            //log.debug("Unbind: value1 = {}, value2 = {}, type = Unidrectional", value1, value2);
        }
    }

    public <T> void registerBidirectional(Property<T> property1, Property<T> property2) {
        bindings.add(new BidirectionalBindingPair<>(property1, property2));
    }

    public <T> void registerUnidirectional(Property<T> property1, ObservableValue<? extends T> property2) {
        bindings.add(new UnidirectionalBindingPair<>(property1, property2));
    }

    public void bind() {
        for (BindingPair pair : bindings)
            pair.bind();
    }

    public void unbind() {
        for (BindingPair pair : bindings)
            pair.unbind();
    }

    public void clear() {
        unbind();
        bindings.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        unbind();
    }
}
