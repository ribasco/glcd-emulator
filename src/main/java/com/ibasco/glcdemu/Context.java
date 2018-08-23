package com.ibasco.glcdemu;

import com.ibasco.glcdemu.model.GlcdConfigApp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListView;

import java.io.File;

import static com.ibasco.glcdemu.constants.Common.APP_CONFIG_PATH;

/**
 * Application Context
 *
 * @author Rafael Ibasco
 */
public class Context {

    private static class InstanceHolder {
        private static Context INSTANCE = new Context();
    }

    private ObjectProperty<GlcdConfigApp> appConfig;

    private GlcdConfigManager configService;

    private GlcdProfileManager profileService;

    private Context() {}

    public GlcdConfigApp getAppConfig() {
        return appConfigProperty().get();
    }

    public ObjectProperty<GlcdConfigApp> appConfigProperty() {
        if (appConfig == null) {
            appConfig = new SimpleObjectProperty<>(getConfigService().getConfig(new File(APP_CONFIG_PATH), GlcdConfigApp.class, new GlcdConfigApp()));
        }
        return appConfig;
    }

    public void setAppConfig(GlcdConfigApp appConfig) {
        appConfigProperty().set(appConfig);
    }

    public GlcdProfileManager getProfileManager() {
        if (profileService == null) {
            profileService = new GlcdProfileManager();
        }
        return profileService;
    }

    public GlcdConfigManager getConfigService() {
        if (configService == null) {
            configService = new GlcdConfigManager();
        }
        return configService;
    }

    public static Context getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
