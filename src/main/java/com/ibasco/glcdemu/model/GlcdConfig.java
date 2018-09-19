package com.ibasco.glcdemu.model;

import com.ibasco.glcdemu.annotations.Auditable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.time.ZonedDateTime;

abstract public class GlcdConfig {

    @Auditable(enabled = false)
    private ObjectProperty<ZonedDateTime> lastUpdated = new SimpleObjectProperty<>(ZonedDateTime.now());

    @Auditable(enabled = false)
    private ObjectProperty<File> file = new SimpleObjectProperty<>();

    public File getFile() {
        return file.get();
    }

    public ObjectProperty<File> fileProperty() {
        return file;
    }

    public void setFile(File file) {
        this.file.set(file);
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated.get();
    }

    public ObjectProperty<ZonedDateTime> lastUpdatedProperty() {
        return lastUpdated;
    }

    public void setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated.set(lastUpdated);
    }
}
