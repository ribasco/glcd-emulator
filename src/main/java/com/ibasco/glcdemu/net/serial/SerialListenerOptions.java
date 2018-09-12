package com.ibasco.glcdemu.net.serial;

import com.ibasco.glcdemu.net.BaseListenerOption;
import com.ibasco.glcdemu.net.ListenerOption;
import com.ibasco.glcdemu.services.SerialPortService;

public class SerialListenerOptions {
    private SerialListenerOptions() {
    }

    public static final ListenerOption<String> SERIAL_PORT_NAME = new BasicSerialListenerOption<>("SERIAL_PORT", String.class);

    public static final ListenerOption<Integer> BAUD_RATE = new BasicSerialListenerOption<>("BAUD_RATE", Integer.class);

    public static final ListenerOption<Integer> PARITY = new BasicSerialListenerOption<>("PARITY", Integer.class);

    public static final ListenerOption<Integer> DATA_BITS = new BasicSerialListenerOption<>("DATA_BITS", Integer.class);

    public static final ListenerOption<Integer> FLOW_CONTROL = new BasicSerialListenerOption<>("FLOW_CONTROL", Integer.class);

    public static final ListenerOption<Integer> STOP_BITS = new BasicSerialListenerOption<>("STOP_BITS", Integer.class);

    public static final ListenerOption<SerialPortService> PORT_SERVICE = new BasicSerialListenerOption<>("PORT_SERVICE", SerialPortService.class);

    private static class BasicSerialListenerOption<T> extends BaseListenerOption<T> {
        BasicSerialListenerOption(String name, Class<T> type) {
            super(name, type);
        }
    }
}
