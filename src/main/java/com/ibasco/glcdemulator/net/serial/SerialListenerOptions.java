/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: SerialListenerOptions.java
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
package com.ibasco.glcdemulator.net.serial;

import com.ibasco.glcdemulator.net.BaseListenerOption;
import com.ibasco.glcdemulator.net.ListenerOption;
import com.ibasco.glcdemulator.services.SerialPortService;

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
