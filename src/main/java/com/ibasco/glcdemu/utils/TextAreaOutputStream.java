package com.ibasco.glcdemu.utils;

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
