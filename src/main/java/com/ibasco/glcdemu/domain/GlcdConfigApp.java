package com.ibasco.glcdemu.domain;

import com.ibasco.glcdemu.annotations.Auditable;
import com.ibasco.glcdemu.utils.NetUtils;

import static com.ibasco.glcdemu.GlcdProfileManager.DEFAULT_PROFILE_DIR_PATH;
import static com.ibasco.glcdemu.GlcdProfileManager.DEFAULT_PROFILE_ID;

@Deprecated
public class GlcdConfigApp extends GlcdConfigCommon {
    private String profileDirPath = DEFAULT_PROFILE_DIR_PATH;

    private int activeProfileId = DEFAULT_PROFILE_ID;

    private int lastGeneratedProfileId = DEFAULT_PROFILE_ID;

    private boolean alwaysOnTop = false;

    private boolean showSettingsPane = false;

    private boolean showPinActivityPane = false;

    @Auditable
    private String lastSavedImagePath = System.getProperty("user.dir");

    @Auditable
    private String lastOpenFilePath = System.getProperty("user.dir");

    private String listenIp = NetUtils.getLocalAddress("127.0.0.1");

    private int listenPort = 3580;

    private double prefWindowWidth = 1024;

    private double prefWindowHeight = 768;

    private double minWindowWidth = 100;

    private double minWindowHeight = 100;

    private boolean confirmOnExit = true;

    private boolean maximized = false;

    private boolean rememberSettingsOnExit = true;

    private boolean autoFitWindowToScreen = false;

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

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop = alwaysOnTop;
    }

    public boolean isShowSettingsPane() {
        return showSettingsPane;
    }

    public void setShowSettingsPane(boolean showSettingsPane) {
        this.showSettingsPane = showSettingsPane;
    }

    public boolean isShowPinActivityPane() {
        return showPinActivityPane;
    }

    public void setShowPinActivityPane(boolean showPinActivityPane) {
        this.showPinActivityPane = showPinActivityPane;
    }

    public String getLastSavedImagePath() {
        return lastSavedImagePath;
    }

    public void setLastSavedImagePath(String lastSavedImagePath) {
        this.lastSavedImagePath = lastSavedImagePath;
    }

    public String getLastOpenFilePath() {
        return lastOpenFilePath;
    }

    public void setLastOpenFilePath(String lastOpenFilePath) {
        this.lastOpenFilePath = lastOpenFilePath;
    }

    public String getListenIp() {
        return listenIp;
    }

    public void setListenIp(String listenIp) {
        this.listenIp = listenIp;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public double getPrefWindowWidth() {
        return prefWindowWidth;
    }

    public void setPrefWindowWidth(double prefWindowWidth) {
        this.prefWindowWidth = prefWindowWidth;
    }

    public double getPrefWindowHeight() {
        return prefWindowHeight;
    }

    public void setPrefWindowHeight(double prefWindowHeight) {
        this.prefWindowHeight = prefWindowHeight;
    }

    public double getMinWindowWidth() {
        return minWindowWidth;
    }

    public void setMinWindowWidth(double minWindowWidth) {
        this.minWindowWidth = minWindowWidth;
    }

    public double getMinWindowHeight() {
        return minWindowHeight;
    }

    public void setMinWindowHeight(double minWindowHeight) {
        this.minWindowHeight = minWindowHeight;
    }

    public boolean isConfirmOnExit() {
        return confirmOnExit;
    }

    public void setConfirmOnExit(boolean confirmOnExit) {
        this.confirmOnExit = confirmOnExit;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    public boolean isRememberSettingsOnExit() {
        return rememberSettingsOnExit;
    }

    public void setRememberSettingsOnExit(boolean rememberSettingsOnExit) {
        this.rememberSettingsOnExit = rememberSettingsOnExit;
    }

    public boolean isAutoFitWindowToScreen() {
        return autoFitWindowToScreen;
    }

    public void setAutoFitWindowToScreen(boolean autoFitWindowToScreen) {
        this.autoFitWindowToScreen = autoFitWindowToScreen;
    }
}
