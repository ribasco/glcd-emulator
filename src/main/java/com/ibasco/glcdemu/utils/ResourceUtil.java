package com.ibasco.glcdemu.utils;

import com.ibasco.glcdemu.GlcdController;
import com.ibasco.glcdemu.Bootstrap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

//TODO: Consider refactoring this, should we have a single accessible registry for controllers?
@SuppressWarnings("Duplicates")
public class ResourceUtil {

    private static FXMLLoader loader;

    private static Callback<Class<?>, Object> controllerFactory;

    private static GlcdController controller;

    public static URL getResource(String resourceName) {
        return Bootstrap.class.getClassLoader().getResource(resourceName);
    }

    public static URL getStylesheet(String stylesheetName) {
        return getResource(String.format("css/%s", stylesheetName));
    }

    public static URL getFxmlResource(String resourceName) {
        return getResource(String.format("views/%s.fxml", resourceName));
    }

    public static Parent loadFxmlResource(String resourceName) throws IOException {
        return loadFxmlResource(resourceName, (GlcdController) null);
    }

    public static Parent loadFxmlResource(String resourceName, Stage stage) throws IOException {
        try {
            URL viewUrl = getFxmlResource(resourceName);
            if (viewUrl == null)
                return null;
            FXMLLoader loader = getLoader(stage);
            loader.setLocation(viewUrl);
            loader.setRoot(null);
            return loader.load();
        } finally {
            controller = loader.getController();
        }
    }

    public static Parent loadFxmlResource(String resourceName, GlcdController controller) throws IOException {
        try {
            URL viewUrl = getFxmlResource(resourceName);
            if (viewUrl == null)
                return null;
            FXMLLoader loader = getLoader(controller.getStage());
            loader.setLocation(viewUrl);
            loader.setRoot(null);
            loader.setController(controller);
            return loader.load();
        } finally {
            controller = loader.getController();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends GlcdController> T getLastController() {
        if (controller == null)
            return null;
        return (T) controller;
    }

    public static FXMLLoader getLoader() {
        return getLoader(null);
    }

    public static FXMLLoader getLoader(Stage stage) {
        if (loader == null) {
            loader = new FXMLLoader();
            loader.setControllerFactory(getControllerFactory(stage));
            loader.setClassLoader(ResourceUtil.class.getClassLoader());
        }
        return loader;
    }

    private static Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        if (controllerFactory == null) {
            controllerFactory = param -> {
                try {
                    return param.getConstructor(Stage.class).newInstance(stage);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    try {
                        return param.newInstance();
                    } catch (InstantiationException | IllegalAccessException e1) {
                        throw new RuntimeException("Unable to produce controller", e);
                    }
                }
            };
        }
        return controllerFactory;
    }
}
