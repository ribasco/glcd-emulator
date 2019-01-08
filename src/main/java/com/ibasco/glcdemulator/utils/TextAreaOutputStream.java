/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: TextAreaOutputStream.java
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
package com.ibasco.glcdemulator.utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {

    private TextArea textArea;

    private StringBuffer buffer = new StringBuffer();

    public TextAreaOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void flush() {
        Platform.runLater(() -> {
            textArea.appendText(buffer.toString());
            buffer.setLength(0);
        });
    }

    @Override
    public void write(int b) {
        buffer.append(String.valueOf((char) b));
    }
}
