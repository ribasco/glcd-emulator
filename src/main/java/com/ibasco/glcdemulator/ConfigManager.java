/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: ConfigManager.java
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
package com.ibasco.glcdemulator;

import static com.ibasco.glcdemulator.constants.Common.APP_CONFIG_PATH;
import com.ibasco.glcdemulator.model.GlcdConfig;
import com.ibasco.glcdemulator.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

/**
 * Service that provides basic CRUD operations for application specific configuration files
 *
 * @author Rafael Ibasco
 */
public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    /**
     * This should only be instatiated from the application context level
     */
    ConfigManager() {
    }

    //<editor-fold desc="Public Methods">
    public void save(GlcdConfig config) throws IOException {
        if (config == null)
            throw new IllegalArgumentException("Config cannot be null");
        save(config, config.getFile());
    }

    public void save(GlcdConfig config, String path) throws IOException {
        if (StringUtils.isBlank(path))
            throw new IOException("Path must not be empty");
        save(config, new File(path));
    }

    public void save(GlcdConfig config, File file) throws IOException {
        if (config == null)
            throw new IllegalArgumentException("Config cannot be null");
        if (file == null) {
            log.warn("File not specified, using default = {}", APP_CONFIG_PATH);
            file = new File(APP_CONFIG_PATH);
        }
        ZonedDateTime tmp = config.getLastUpdated();
        config.setLastUpdated(ZonedDateTime.now());
        String jsonSettings = JsonUtils.toJson(config);
        try {
            FileUtils.writeStringToFile(file, jsonSettings, StandardCharsets.UTF_8, false);
            config.setFile(file);
        } catch (IOException e) {
            config.setLastUpdated(tmp);
            throw e;
        }
        log.info("Saved Settings to {}, Last Updated = {}", file.getAbsolutePath(), config.getLastUpdated());
    }

    public <T extends GlcdConfig> T getConfig(String path, Class<T> configType) {
        return getConfig(path, configType, null);
    }

    public <T extends GlcdConfig> T getConfig(String path, Class<T> configType, T defaultConfig) {
        return getConfig(new File(path), configType, defaultConfig);
    }

    public <T extends GlcdConfig> T getConfig(File file, Class<T> configType) {
        return getConfig(file, configType, null);
    }

    public <T extends GlcdConfig> T getConfig(File file, Class<T> configType, T defaultConfig) {
        try {
            if (file == null)
                throw new IllegalArgumentException("Config file cannot be null");
            if (file.exists()) {
                String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                T config = JsonUtils.fromJson(json, configType);
                config.setFile(file);
                return config;
            }
        } catch (IOException e) {
            log.warn("An error occured while loading config file '" + file.getPath() + "', using default", e);
        }
        return defaultConfig;
    }
    //</editor-fold>
}
