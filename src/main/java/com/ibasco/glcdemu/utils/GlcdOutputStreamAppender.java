/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdOutputStreamAppender.java
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
package com.ibasco.glcdemu.utils;

import ch.qos.logback.core.OutputStreamAppender;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GlcdOutputStreamAppender<E> extends OutputStreamAppender<E> {
    private static final DelegatingOutputStream DELEGATING_OUTPUT_STREAM = new DelegatingOutputStream(null);

    @Override
    public void start() {
        setOutputStream(DELEGATING_OUTPUT_STREAM);
        super.start();
        System.out.println("START");
    }

    @Override
    public void stop() {
        super.stop();
        System.out.println("STOP");
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DELEGATING_OUTPUT_STREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {
        public DelegatingOutputStream(OutputStream out) {
            super(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            });
        }

        void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }
}
