/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: ScannerService.java
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
package com.ibasco.glcdemulator.services;

import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.emulator.GlcdEmulator;
import com.ibasco.glcdemulator.utils.GlcdUtil;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdControllerType;
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
