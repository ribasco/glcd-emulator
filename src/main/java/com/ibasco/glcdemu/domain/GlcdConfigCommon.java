package com.ibasco.glcdemu.domain;

import com.ibasco.glcdemu.annotations.Exclude;
import com.ibasco.glcdemu.annotations.Auditable;

import java.io.File;
import java.time.ZonedDateTime;

@Deprecated
abstract public class GlcdConfigCommon implements GlcdConfig {

    @Auditable
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
