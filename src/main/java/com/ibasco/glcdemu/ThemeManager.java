/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: ThemeManager.java
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
package com.ibasco.glcdemu;

import com.ibasco.glcdemu.exceptions.ThemeNotFoundException;
import com.ibasco.glcdemu.utils.ResourceUtil;
import com.sun.javafx.stage.StageHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Managers application theme
 *
 * @author Rafael Ibasco
 */
public class ThemeManager {

    private Map<String, String> themeMap = new HashMap<>();

    private StringProperty activeTheme = new SimpleStringProperty("menuThemeDefault") {
        @Override
        protected void invalidated() {
            //applyTheme(get());
        }
    };

    private static final String THEME_DEFAULT = "app.css";

    private static final String THEME_DEFAULT_DARK = "app-dark.css";

    private static final Logger log = LoggerFactory.getLogger(ThemeManager.class);

    ThemeManager() {
        //Add built-in themes
        themeMap.put("menuThemeDefault", ResourceUtil.getStylesheet(THEME_DEFAULT).toExternalForm());
        themeMap.put("menuThemeDark", ResourceUtil.getStylesheet(THEME_DEFAULT_DARK).toExternalForm());
        activeTheme.bindBidirectional(Context.getInstance().getAppConfig().themeIdProperty());
        registerExternalThemes();
    }

    /**
     * Reads the config file for registered external themes and stores them into the internal map
     */
    private void registerExternalThemes() {
        //TODO: Implement
    }

    public void register(String themeId, File file) {
        try {
            themeMap.put(themeId, file.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not register theme", e);
        }
    }

    public String unregister(String themeId) {
        return themeMap.remove(themeId);
    }

    public Collection<String> getThemes() {
        return themeMap.values();
    }

    /**
     * Applies the theme on the primary scene of the application
     *
     * @param themeId
     *         The themeId to be applied
     */
    public void applyTheme(String themeId) {
        applyTheme(Context.getPrimaryStage().getScene(), themeId);
    }

    public void applyTheme(Scene scene) {
        applyTheme(scene, getActiveTheme());
    }

    /**
     * Applies the theme on the provided stage
     *
     * @param scene
     *         The {@link Stage} to be applied with the theme
     * @param themeId
     *         The themeId to be applied
     */
    public void applyTheme(Scene scene, String themeId) {
        if (!themeMap.containsKey(themeId))
            throw new ThemeNotFoundException("Could not locate theme id '" + themeId + "'");
        scene.getStylesheets().clear();
        scene.getStylesheets().add(themeMap.get(themeId));
        log.debug("Applied theme id: {} to scene: {}", themeId, scene);
    }

    public void applyToAll() {
        for (Stage stage : StageHelper.getStages()) {
            if (stage.getScene() != null) {
                applyTheme(stage.getScene());
            }
        }
    }

    public String getActiveTheme() {
        return activeThemeProperty().get();
    }

    public StringProperty activeThemeProperty() {
        return activeTheme;
    }

    public void setActiveTheme(String activeTheme) {
        activeThemeProperty().set(activeTheme);
    }

    private String resolveStylesheet(String stylesheetFileName) {
        return ResourceUtil.getStylesheet(stylesheetFileName).toExternalForm();
    }
}
