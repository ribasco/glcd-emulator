package com.ibasco.glcdemu.net.tcp;

import com.ibasco.glcdemu.net.ListenerOption;

public class TcpListenerOptions {
    private TcpListenerOptions() {
    }

    public static final ListenerOption<String> IP_ADDRESS = new BasicTcpListenerOption<>("IP_ADDRESS", String.class);

    public static final ListenerOption<Integer> PORT_NUMBER = new BasicTcpListenerOption<>("PORT_NUMBER", Integer.class);

    private static class BasicTcpListenerOption<T> implements ListenerOption<T> {

        private String name;

        private Class<T> type;

        private BasicTcpListenerOption(String name, Class<T> type) {
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
}
