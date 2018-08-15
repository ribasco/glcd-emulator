package com.ibasco.glcdemu;

@FunctionalInterface
public interface GpioEventListener {
    void onGpioEvent(GpioActivity event);
}
