/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: TcpListenerOptions.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 - 2019 Rafael Luis Ibasco
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
package com.ibasco.glcdemulator.net.tcp;

import com.ibasco.glcdemulator.net.ListenerOption;

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
