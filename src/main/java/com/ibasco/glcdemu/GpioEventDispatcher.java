package com.ibasco.glcdemu;

import java.util.ArrayList;
import java.util.List;

abstract public class GpioEventDispatcher {

    private List<GpioEventListener> listeners = new ArrayList<>();

    public abstract void initialize();

    protected void dispatchGpioEvent(GpioActivity event) {
        for (GpioEventListener listener : listeners) {
            listener.onGpioEvent(event);
        }
    }

    public void addListener(GpioEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(GpioEventListener listener) {
        this.listeners.remove(listener);
    }
}
