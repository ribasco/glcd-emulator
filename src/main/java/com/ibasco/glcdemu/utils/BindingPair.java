package com.ibasco.glcdemu.utils;

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
}
