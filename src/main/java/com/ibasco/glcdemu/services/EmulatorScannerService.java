package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.Context;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EmulatorScannerService extends Service<ObservableList<Class<?>>> {

    private static final Logger log = LoggerFactory.getLogger(EmulatorScannerService.class);

    public EmulatorScannerService() {
        setExecutor(Context.getTaskExecutor());
    }

    @Override
    protected Task<ObservableList<Class<?>>> createTask() {
        return new Task<ObservableList<Class<?>>>() {
            @Override
            protected ObservableList<Class<?>> call() throws Exception {
                List<Class<?>> emulatorScanResult;

                try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages("com.ibasco").scan()) {
                    ClassInfoList classInfo = scanResult.getClassesImplementing("com.ibasco.glcdemu.emulator.GlcdEmulator").filter(f -> !f.isAbstract());
                    //emulatorScanResult = classInfo.getNames();
                    emulatorScanResult = classInfo.loadClasses();
                    log.debug("Got {} results from the scan", emulatorScanResult.size());
                    for (Class<?> classResult : emulatorScanResult) {
                        log.debug("\tClass: {}", classResult);
                    }

                }
                return FXCollections.observableArrayList(emulatorScanResult);
            }
        };
    }
}
