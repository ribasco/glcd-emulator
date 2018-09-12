package com.ibasco.glcdemu.net;

public interface ListenerOption<T> {
    String name();

    Class<T> type();
}
