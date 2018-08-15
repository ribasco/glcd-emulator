package com.ibasco.glcdemu.beans;

import java.io.File;
import java.time.ZonedDateTime;

public interface GlcdConfig {
    ZonedDateTime getLastUpdated();

    void setLastUpdated(ZonedDateTime lastUpdated);

    File getFile();

    void setFile(File file);
}
