package com.ibasco.glcdemu.enums;

import com.ibasco.glcdemu.net.EmulatorListenerTask;
import com.ibasco.glcdemu.net.serial.SerialEmulatorListenerTask;
import com.ibasco.glcdemu.net.tcp.TcpEmulatorListenerTask;

public enum ConnectionType {
    SERIAL("Serial", "Serial Connection", SerialEmulatorListenerTask.class),
    TCP("TCP", "TCP Connection", TcpEmulatorListenerTask.class);

    private final String name;
    private final String description;
    private Class<? extends EmulatorListenerTask> listenerClass;

    ConnectionType(String name, String description, Class<? extends EmulatorListenerTask> listenerClass) {
        this.name = name;
        this.description = description;
        this.listenerClass = listenerClass;
    }

    public Class<? extends EmulatorListenerTask> getListenerClass() {
        return listenerClass;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
