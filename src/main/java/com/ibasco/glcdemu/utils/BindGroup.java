package com.ibasco.glcdemu.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a group of bindable bindings. This allows simultaneous binding/unbinding of bindings.
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

    public void attach() {
        for (BindingPair pair : bindings)
            pair.bind();
    }

    public void detach() {
        for (BindingPair pair : bindings)
            pair.unbind();
    }

    public void clear() {
        detach();
        bindings.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        detach();
    }
}
