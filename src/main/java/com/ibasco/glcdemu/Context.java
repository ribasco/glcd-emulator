package com.ibasco.glcdemu;

import com.ibasco.glcdemu.model.GlcdConfigApp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ListView;

import java.io.File;

import static com.ibasco.glcdemu.constants.Common.APP_CONFIG_PATH;

/**
 * Application Context
 *
 * @author Rafael Ibasco
 */
public final class Context {

    private static class InstanceHolder {
        private static Context INSTANCE = new Context();
    }

    private ReadOnlyObjectWrapper<GlcdConfigApp> appConfig;

    private GlcdConfigManager configService;

    private GlcdProfileManager profileService;

    private Context() {}

    public final GlcdConfigApp getAppConfig() {
        return appConfigProperty().get();
    }

    public final ReadOnlyObjectProperty<GlcdConfigApp> appConfigProperty() {
        if (appConfig == null) {
            GlcdConfigApp config = getConfigService().getConfig(new File(APP_CONFIG_PATH), GlcdConfigApp.class, new GlcdConfigApp());
            appConfig = new ReadOnlyObjectWrapper<>(config);
        }
        return appConfig.getReadOnlyProperty();
    }

    public final GlcdProfileManager getProfileManager() {
        if (profileService == null) {
            profileService = new GlcdProfileManager();
        }
        return profileService;
    }

    public final GlcdConfigManager getConfigService() {
        if (configService == null) {
            configService = new GlcdConfigManager();
        }
        return configService;
    }

    public static Context getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
