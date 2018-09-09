package com.ibasco.glcdemu;

import com.ibasco.glcdemu.model.GlcdConfigApp;
import javafx.application.HostServices;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

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

    void setHostServices(HostServices services) {
        this.hostServices = services;
    }
}
