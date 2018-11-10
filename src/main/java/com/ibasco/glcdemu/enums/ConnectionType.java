/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: ConnectionType.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Luis Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
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
