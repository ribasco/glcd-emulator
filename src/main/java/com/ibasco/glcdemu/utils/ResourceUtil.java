package com.ibasco.glcdemu.utils;

import com.ibasco.glcdemu.Main;

import java.net.URL;

public class ResourceUtil {
    public static URL getResource(String resourceName) {
        return Main.class.getClassLoader().getResource(resourceName);
    }

    public static URL getStylesheet(String stylesheetName) {
        return getResource( String.format("css/%s", stylesheetName));
    }

    public static URL getFxmlResource(String resourceName) {
        return getResource( String.format("views/%s.fxml", resourceName));
    }
}
