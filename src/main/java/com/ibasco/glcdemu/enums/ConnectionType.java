package com.ibasco.glcdemu.enums;

import com.ibasco.glcdemu.net.RemoteListenerTask;
import com.ibasco.glcdemu.net.serial.SerialRemoteListenerTask;
import com.ibasco.glcdemu.net.tcp.TcpRemoteListenerTask;

public enum ConnectionType {
    SERIAL("Serial", "Serial Connection", SerialRemoteListenerTask.class),
    TCP("TCP", "TCP Connection", TcpRemoteListenerTask.class);

    private final String name;
    private final String description;
    private Class<? extends RemoteListenerTask> listenerClass;

    ConnectionType(String name, String description, Class<? extends RemoteListenerTask> listenerClass) {
        this.name = name;
        this.description = description;
        this.listenerClass = listenerClass;
    }

    public Class<? extends RemoteListenerTask> getListenerClass() {
        return listenerClass;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
