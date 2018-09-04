package com.ibasco.glcdemu.utils;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class UIUtil {

    public static String toHexString(Color color) throws NullPointerException {
        return String.format("#%02x%02x%02x",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()));
    }

    public static double computeStringWidth(String text, Font font) {
        final Text tmp = new Text(text);
        tmp.setFont(font);
        return tmp.getLayoutBounds().getWidth();
    }

    public static double computeStringHeight(String text, Font font) {
        final Text tmp = new Text(text);
        tmp.setFont(font);
        return tmp.getLayoutBounds().getHeight();
    }
}
