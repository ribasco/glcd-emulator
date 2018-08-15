package com.ibasco.glcdemu.services;

import com.google.gson.*;
import com.ibasco.glcdemu.annotations.Exclude;
import com.ibasco.glcdemu.beans.GlcdConfig;
import com.ibasco.glcdemu.beans.GlcdConfigApp;
import com.ibasco.glcdemu.utils.UIUtil;
import javafx.scene.paint.Color;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class GlcdConfigService {

    private static final Logger log = LoggerFactory.getLogger(GlcdConfigService.class);

    private static final String APP_CONFIG_FILE = "settings.json";

    private static final String APP_CONFIG_PATH = System.getProperty("user.dir") + File.separator + APP_CONFIG_FILE;

    private Gson gson;

    private GlcdConfigApp appConfig;

    private final GlcdConfigProfileService profileManager = new GlcdConfigProfileService();

    private static class InstanceHolder {
        private static GlcdConfigService INSTANCE = new GlcdConfigService();
    }

    private static class ZonedDateTimeAdapter implements JsonDeserializer<ZonedDateTime>, JsonSerializer<ZonedDateTime> {
        @Override
        public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ZonedDateTime.ofInstant(Instant.ofEpochSecond(json.getAsLong()), ZoneId.systemDefault());
        }

        @Override
        public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toEpochSecond());
        }
    }

    private static class ColorTypeAdapter implements JsonDeserializer<Color>, JsonSerializer<Color> {
        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String str = json.getAsString();
            return Color.web(str);
        }

        @Override
        public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(UIUtil.toHexString(src));
        }
    }

    private GlcdConfigService() {
        GsonBuilder builder = new GsonBuilder();
        ExclusionStrategy excludeAnnotation = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getAnnotation(Exclude.class) != null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
        builder.addSerializationExclusionStrategy(excludeAnnotation);
        builder.addDeserializationExclusionStrategy(excludeAnnotation);
        builder.registerTypeAdapter(Color.class, new ColorTypeAdapter());
        builder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter());
        builder.setPrettyPrinting();
        gson = builder.create();
    }

    public static void save(GlcdConfig config, String path) throws IOException {
        if (StringUtils.isBlank(path))
            throw new IOException("Path must not be empty");
        save(config, new File(path));
    }

    public static void save(GlcdConfig config, File file) throws IOException {
        InstanceHolder.INSTANCE._save(config, file);
    }

    public static void save(GlcdConfigApp config) throws IOException {
        save(config, APP_CONFIG_PATH);
    }

    public static void update(GlcdConfigApp config) {
        InstanceHolder.INSTANCE._update(config, InstanceHolder.INSTANCE.appConfig);
    }

    public static void update(GlcdConfig source, GlcdConfig dest) {
        InstanceHolder.INSTANCE._update(source, dest);
    }

    public static <T extends GlcdConfig> T getConfig(String path, Class<T> configType) {
        return getConfig(path, configType, null);
    }

    public static <T extends GlcdConfig> T getConfig(String path, Class<T> configType, T defaultConfig) {
        return getConfig(new File(path), configType, defaultConfig);
    }

    public static <T extends GlcdConfig> T getConfig(File file, Class<T> configType) {
        return getConfig(file, configType, null);
    }

    public static <T extends GlcdConfig> T getConfig(File file, Class<T> configType, T defaultConfig) {
        return InstanceHolder.INSTANCE._loadConfig(file, configType, defaultConfig);
    }

    public static GlcdConfigApp getAppConfig() {
        return InstanceHolder.INSTANCE._loadAppConfig();
    }

    public static GlcdConfigProfileService getProfileManager() {
        return InstanceHolder.INSTANCE.profileManager;
    }

    private void _save(GlcdConfig config, File file) throws IOException {
        if (config == null)
            throw new NullPointerException("Config cannot be null");
        ZonedDateTime tmp = config.getLastUpdated();
        config.setLastUpdated(ZonedDateTime.now());
        String jsonSettings = gson.toJson(config);
        try {
            FileUtils.writeStringToFile(file, jsonSettings, StandardCharsets.UTF_8, false);
        } catch (IOException e) {
            config.setLastUpdated(tmp);
            throw e;
        }
        log.info("Saved Settings to {}, Last Updated = {}", file.getAbsolutePath(), config.getLastUpdated());
    }

    private void _update(GlcdConfig source, GlcdConfig destination) {
        try {
            BeanUtils.copyProperties(destination, source);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to copy bean properties", e);
        }
    }

    private <T extends GlcdConfig> T _loadConfig(File configFile, Class<T> type, T defaultConfig) {
        try {
            if (configFile == null)
                throw new NullPointerException("Config file cannot be null");
            if (!configFile.exists())
                throw new FileNotFoundException("Configuration file '" + configFile.getPath() + "' does not exist");
            String json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
            T config = gson.fromJson(json, type);
            config.setFile(configFile);
            return config;
        } catch (IOException e) {
            log.warn("An error occured while loading config file '" + configFile.getPath() + "', using default", e);
        }
        return defaultConfig;
    }

    private GlcdConfigApp _loadAppConfig() {
        if (appConfig == null) {
            File configFile = new File(System.getProperty("user.dir") + File.separator + APP_CONFIG_FILE);
            appConfig = _loadConfig(configFile, GlcdConfigApp.class, new GlcdConfigApp());
        }
        return appConfig;
    }
}
