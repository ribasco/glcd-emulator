package com.ibasco.glcdemu.utils;

public interface WritablePixelBuffer {
    void write(int x, int y, int state);

    void write(int x, int y, boolean state);
}
