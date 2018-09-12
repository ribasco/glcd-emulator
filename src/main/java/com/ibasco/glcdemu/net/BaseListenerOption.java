package com.ibasco.glcdemu.net;

abstract public class BaseListenerOption<T> implements ListenerOption<T> {
    private String name;
    private Class<T> type;

    public BaseListenerOption(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }
}
