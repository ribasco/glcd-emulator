/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: Context.java
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

import static com.ibasco.glcdemu.constants.Common.APP_CONFIG_PATH;
import com.ibasco.glcdemu.model.GlcdConfigApp;
import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.application.HostServices;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * Application Context
 *
 * @author Rafael Ibasco
 */
public final class Context {

    private static final Logger log = LoggerFactory.getLogger(Context.class);

    private static class InstanceHolder {
        private static Context INSTANCE = new Context();
    }

    private ReadOnlyObjectWrapper<GlcdConfigApp> appConfig;

    private GlcdConfigManager configService;

    private GlcdProfileManager profileService;

    private ExecutorService taskExecutor;

    private ThemeManager themeManager;

    private HostServices hostServices;

    private Context() {
    }

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

    public static ExecutorService getTaskExecutor() {
        if (getInstance().taskExecutor == null) {
            final ForkJoinPool.ForkJoinWorkerThreadFactory forkThreadFactory = pool -> {
                final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                worker.setName("glcd-task-" + worker.getPoolIndex());
                return worker;
            };
            getInstance().taskExecutor = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), forkThreadFactory, null, true);
        }
        return getInstance().taskExecutor;
    }

    public HostServices getHostServices() {
        return getInstance().hostServices;
    }

    public ThemeManager getThemeManager() {
        if (themeManager == null) {
            themeManager = new ThemeManager();
        }
        return themeManager;
    }

    public static Stage getPrimaryStage() {
        return Stages.getPrimaryStage();
    }

    public static Context getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static String getAppVersion() {
        String version = Context.class.getPackage().getImplementationVersion();
        if (StringUtils.isBlank(version)) {
            try {
                URI versionFile = ResourceUtil.getResource("version.properties").toURI();
                Properties appProperties = new Properties();
                appProperties.load(new FileReader(new File(versionFile)));
                version = appProperties.getProperty("version");
            } catch (URISyntaxException | IOException e) {
                log.error("Error loading version property file", e);
            }
        }
        return version;
    }

    void setHostServices(HostServices services) {
        this.hostServices = services;
    }
}
