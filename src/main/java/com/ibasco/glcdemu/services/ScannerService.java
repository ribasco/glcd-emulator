package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
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

public class ScannerService extends Service<ObservableList<Class<? extends GlcdEmulator>>> {

    private static final Logger log = LoggerFactory.getLogger(ScannerService.class);

    private ObservableList<Class<? extends GlcdEmulator>> cache = FXCollections.observableArrayList();

    private final AtomicBoolean forceRefresh = new AtomicBoolean(false);

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
    protected Task<ObservableList<Class<? extends GlcdEmulator>>> createTask() {
        return new Task<ObservableList<Class<? extends GlcdEmulator>>>() {
            @Override
            protected ObservableList<Class<? extends GlcdEmulator>> call() {
                if (!cache.isEmpty() && !forceRefresh.get()) {
                    log.debug("Returning cached entries");
                    return cache;
                }

                log.debug("Refreshing cached item(s)");
                try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages("com.ibasco").scan()) {
                    ClassInfoList classInfo = scanResult.getClassesImplementing("com.ibasco.glcdemu.emulator.GlcdEmulator").filter(f -> !f.isAbstract());
                    List<Class<GlcdEmulator>> result = classInfo.loadClasses(GlcdEmulator.class);
                    if (!result.isEmpty()) {
                        cache.clear();
                        cache.addAll(result);
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
