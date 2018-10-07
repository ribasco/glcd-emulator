package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.annotations.Emulator;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.utils.GlcdUtil;
import com.ibasco.pidisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdControllerType;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class-path scanner service for available {@link GlcdDisplay}
 *
 * @author Rafael Ibasco
 */
public class ScannerService extends Service<ObservableList<GlcdDisplay>> {

    private static final Logger log = LoggerFactory.getLogger(ScannerService.class);

    private ObservableList<GlcdDisplay> cache = FXCollections.observableArrayList();

    private final AtomicBoolean forceRefresh = new AtomicBoolean(false);

    private static final String WHITELIST_PACKAGE = "com.ibasco";

    public ScannerService() {
        setExecutor(Context.getTaskExecutor());
    }

    public boolean isForceRefresh() {
        return forceRefresh.get();
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh.set(forceRefresh);
    }

    @Override
    protected Task<ObservableList<GlcdDisplay>> createTask() {
        return new Task<ObservableList<GlcdDisplay>>() {
            @Override
            protected ObservableList<GlcdDisplay> call() {
                if (!cache.isEmpty() && !forceRefresh.get()) {
                    log.debug("Returning cached entries");
                    return cache;
                }
                try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(WHITELIST_PACKAGE).scan()) {
                    ClassInfoList classInfo = scanResult.getClassesImplementing(GlcdEmulator.class.getName()).filter(f -> !f.isAbstract() && f.hasAnnotation(Emulator.class.getName()));
                    List<Class<GlcdEmulator>> result = classInfo.loadClasses(GlcdEmulator.class);

                    if (!result.isEmpty()) {
                        cache.clear();
                        for (Class<GlcdEmulator> emulatorClass : result) {
                            GlcdControllerType type = emulatorClass.getAnnotation(Emulator.class).controller();
                            List<GlcdDisplay> displayList = GlcdUtil.findDisplay(d -> d.getController().equals(type));
                            if (displayList != null && !displayList.isEmpty())
                                cache.addAll(displayList);
                        }
                    }
                    log.debug("Refreshed {} cached item(s)", cache.size());
                } finally {
                    Platform.runLater(() -> forceRefresh.set(false));
                }
                return cache;
            }
        };
    }
}
