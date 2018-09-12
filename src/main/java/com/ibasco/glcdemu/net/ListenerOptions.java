package com.ibasco.glcdemu.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class ListenerOptions {
    private Map<ListenerOption, Object> options = new HashMap<>();

    public ListenerOptions() {
    }

    public <T> ListenerOptions put(ListenerOption<T> option, T value) {
        options.put(option, value);
        return this;
    }

    public <T> T get(ListenerOption<T> option) {
        return (T) options.get(option);
    }

    public Set<ListenerOption> getOptions() {
        return options.keySet();
    }
}
