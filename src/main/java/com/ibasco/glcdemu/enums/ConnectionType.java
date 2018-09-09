package com.ibasco.glcdemu.enums;

public enum ConnectionType {
    SERIAL("Serial", "Serial Connection"),
    TCP("TCP", "TCP Connection");

    private final String name;
    private final String description;

    ConnectionType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
