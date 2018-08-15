package com.ibasco.glcdemu.utils;

import javafx.scene.paint.Color;

public class UIUtil {

    public static String toHexString(Color color) throws NullPointerException {
        return String.format("#%02x%02x%02x",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()));
    }
}
