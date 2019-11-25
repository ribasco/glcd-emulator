/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: ResourceUtil.java
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

import com.ibasco.glcdemulator.Bootstrap;
import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.Controller;
import com.ibasco.glcdemulator.Controllers;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Helper class for loading application resource files (e.g. fxml stylesheets etc). This class is NOT thread-safe
 *
 * @author Rafael Ibasco
 */
@SuppressWarnings("Duplicates")
public class ResourceUtil {

    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    private static AtomicReference<Parent> lastRootNode = new AtomicReference<>();

    public static InputStream getResourceAsStream(String resourceName) {
        log.debug("getResourceAsStream() : Getting resource: {}", resourceName);
        return Objects.requireNonNull(Bootstrap.class.getResourceAsStream(resourceName));
    }

    public static URL getResource(String resourceName) {
        return Bootstrap.class.getResource(resourceName);
    }

    public static URL getStylesheet(String stylesheetName) {
        return getResource(String.format("/css/%s", stylesheetName));
    }

    public static URL getFxmlResource(String resourceName) {
        return getResource(String.format("/views/%s.fxml", resourceName));
    }

    public static byte[] readResourceAsBytes(String resource) {
        try {
            return IOUtils.toByteArray(getResourceAsStream(resource));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read resource", e);
        }
    }

    public static String readFileResource(String fileName) {
        try {
            InputStream is = getResourceAsStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("Could not read file resource", e);
        }
        return null;
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
        FXMLLoader loader = new FXMLLoader();
        T node = null;
        try {
            URL viewUrl = getFxmlResource(resourceName);
            if (viewUrl == null)
                return null;
            loader.setClassLoader(ResourceUtil.class.getClassLoader());
            loader.setControllerFactory(Controllers::getController);
            loader.setLocation(viewUrl);
            loader.setRoot(null);
            node = loader.load();
            return node;
        } finally {
            lastRootNode.set(node);
        }
    }

    /**
     * <p>Loads an FXML resource file with the controller specified. This assumes that no fx:controller attribute has been explicitly set on the FXML resource, otherwise an exception will be thrown.</p>
     *
     * @param resourceName
     *         The FXML resource name (excluding the extension)
     * @param controller
     *         The {@link Controller} wthat will be assigned for the resource
     * @param <T>
     *         Any subclass of {@link Parent} node
     *
     * @return The {@link javafx.scene.Node} or {@link Parent} root instance of the view
     */
    @Deprecated
    public static <T extends Parent> T loadFxmlResource(String resourceName, Controller controller) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        T node = null;
        try {
            URL viewUrl = getFxmlResource(resourceName);
            if (viewUrl == null)
                return null;
            loader.setClassLoader(ResourceUtil.class.getClassLoader());
            loader.setControllerFactory(Controllers::getController);
            loader.setLocation(viewUrl);
            loader.setRoot(null);
            if (controller != null)
                loader.setController(controller);
            node = loader.load();
            return node;
        } finally {
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
}
