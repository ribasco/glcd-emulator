/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: ResourceUtil.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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

import com.ibasco.glcdemu.Bootstrap;
import com.ibasco.glcdemu.GlcdController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Helper class for loading application resource files (e.g. fxml stylesheets etc). This class is NOT thread-safe
 *
 * @author Rafael Ibasco
 */
@SuppressWarnings("Duplicates")
public class ResourceUtil {

    private static Callback<Class<?>, Object> controllerFactory;

    private static AtomicReference<GlcdController> lastController = new AtomicReference<>();

    private static AtomicReference<Parent> lastRootNode = new AtomicReference<>();

    public static URL getResource(String resourceName) {
        return Bootstrap.class.getClassLoader().getResource(resourceName);
    }

    public static URL getStylesheet(String stylesheetName) {
        return getResource(String.format("css/%s", stylesheetName));
    }

    public static URL getFxmlResource(String resourceName) {
        return getResource(String.format("views/%s.fxml", resourceName));
    }


    /**
     * <p>Loads an FXML resource file with the controller specified. Using this method requires that you already have an fx:controller attribute assigned for the FXML file you are loading.</p>
     *
     * @param resourceName
     *         The FXML resource name (excluding the extension)
     * @param <T>
     *         Any subclass of {@link Parent} node
     *
     * @return The {@link javafx.scene.Node} or {@link Parent} root instance of the view
     */
    public static <T extends Parent> T loadFxmlResource(String resourceName) throws IOException {
        return loadFxmlResource(resourceName, null);
    }

    /**
     * <p>Loads an FXML resource file with the controller specified. This assumes that no fx:controller attribute has been explicitly set on the FXML resource, otherwise an exception will be thrown.</p>
     *
     * @param resourceName
     *         The FXML resource name (excluding the extension)
     * @param controller
     *         The {@link GlcdController} wthat will be assigned for the resource
     * @param <T>
     *         Any subclass of {@link Parent} node
     *
     * @return The {@link javafx.scene.Node} or {@link Parent} root instance of the view
     */
    public static <T extends Parent> T loadFxmlResource(String resourceName, GlcdController controller) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        T node = null;
        try {
            URL viewUrl = getFxmlResource(resourceName);
            if (viewUrl == null)
                return null;
            loader.setClassLoader(ResourceUtil.class.getClassLoader());
            loader.setLocation(viewUrl);
            loader.setRoot(null);
            if (controller != null)
                loader.setController(controller);
            node = loader.load();
            return node;
        } finally {
            lastController.set(loader.getController());
            lastRootNode.set(node);
        }
    }

    /**
     * Get the last {@link Parent} node returned by {@link #loadFxmlResource}.
     *
     * @return The last {@link Parent} node returned
     */
    public static Parent getLastRootNode() {
        return lastRootNode.get();
    }

    /**
     * Get the last {@link GlcdController} instance returned by {@link #loadFxmlResource}.
     *
     * @param <T>
     *         Any valid subclass of {@link GlcdController}
     *
     * @return An instance of {@link GlcdController}
     */
    @SuppressWarnings("unchecked")
    public static <T extends GlcdController> T getLastController() {
        if (lastController == null)
            return null;
        return (T) lastController.get();
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
