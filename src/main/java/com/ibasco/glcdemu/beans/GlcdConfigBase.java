package com.ibasco.glcdemu.beans;

import com.ibasco.glcdemu.annotations.Exclude;
import com.ibasco.glcdemu.annotations.NoCompare;

import java.io.File;
import java.time.ZonedDateTime;

abstract public class GlcdConfigBase implements GlcdConfig {

    @NoCompare
    private ZonedDateTime lastUpdated = ZonedDateTime.now();

    @Exclude
    private File file;

    @Override
    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public void setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }
}
