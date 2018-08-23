package com.ibasco.glcdemu.domain;

import java.io.File;
import java.time.ZonedDateTime;

@Deprecated
public interface GlcdConfig {
    ZonedDateTime getLastUpdated();

    void setLastUpdated(ZonedDateTime lastUpdated);

    File getFile();

    void setFile(File file);
}
