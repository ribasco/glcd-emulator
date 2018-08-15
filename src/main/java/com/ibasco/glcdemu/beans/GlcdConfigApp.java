package com.ibasco.glcdemu.beans;

import static com.ibasco.glcdemu.services.GlcdConfigProfileService.DEFAULT_PROFILE_DIR_PATH;
import static com.ibasco.glcdemu.services.GlcdConfigProfileService.DEFAULT_PROFILE_ID;

public class GlcdConfigApp extends GlcdConfigBase {
    private String profileDirPath = DEFAULT_PROFILE_DIR_PATH;

    private int activeProfileId = DEFAULT_PROFILE_ID;

    private int lastGeneratedProfileId = DEFAULT_PROFILE_ID;

    public String getProfileDirPath() {
        return profileDirPath;
    }

    public void setProfileDirPath(String profileDirPath) {
        this.profileDirPath = profileDirPath;
    }

    public int getActiveProfileId() {
        return activeProfileId;
    }

    public void setActiveProfileId(int activeProfileId) {
        this.activeProfileId = activeProfileId;
    }

    public int getLastGeneratedProfileId() {
        return lastGeneratedProfileId;
    }

    public int nextId() {
        return lastGeneratedProfileId++;
    }

    public void setLastGeneratedProfileId(int lastGeneratedProfileId) {
        this.lastGeneratedProfileId = lastGeneratedProfileId;
    }
}
