package com.ibasco.glcdemu.model;

import com.ibasco.glcdemu.constants.Common;
import com.ibasco.glcdemu.utils.NetUtils;
import javafx.beans.property.*;

import java.io.File;

import static com.ibasco.glcdemu.GlcdProfileManager.DEFAULT_PROFILE_DIR_PATH;
import static com.ibasco.glcdemu.GlcdProfileManager.DEFAULT_PROFILE_ID;

public class GlcdConfigApp extends GlcdConfig {

    //<editor-fold desc="Property Definitions">
    private StringProperty profileDirPath = new SimpleStringProperty(DEFAULT_PROFILE_DIR_PATH);

    private IntegerProperty defaultProfileId = new SimpleIntegerProperty(DEFAULT_PROFILE_ID);

    private IntegerProperty lastGeneratedProfileId = new SimpleIntegerProperty(DEFAULT_PROFILE_ID);

    private BooleanProperty alwaysOnTop = new SimpleBooleanProperty(false);

    private BooleanProperty toolbarVisible = new SimpleBooleanProperty(true);

    private BooleanProperty showSettingsPane = new SimpleBooleanProperty(false);

    private BooleanProperty showPinActivityPane = new SimpleBooleanProperty(false);

    private StringProperty lastSavedImagePath = new SimpleStringProperty(Common.USER_DIR);

    private StringProperty lastOpenFilePath = new SimpleStringProperty(Common.USER_DIR);

    private StringProperty listenIp = new SimpleStringProperty(NetUtils.getLocalAddress("127.0.0.1"));

    private ObjectProperty<Integer> listenPort = new SimpleObjectProperty<>(3580);

    private DoubleProperty prefWindowWidth = new SimpleDoubleProperty(1024);

    private DoubleProperty prefWindowHeight = new SimpleDoubleProperty(768);

    private DoubleProperty minWindowWidth = new SimpleDoubleProperty(100);

    private DoubleProperty minWindowHeight = new SimpleDoubleProperty(100);

    private BooleanProperty confirmOnExit = new SimpleBooleanProperty(true);

    private BooleanProperty maximized = new SimpleBooleanProperty(false);

    private BooleanProperty rememberSettingsOnExit = new SimpleBooleanProperty(true);

    private SimpleBooleanProperty autoFitWindowToScreen = new SimpleBooleanProperty(false);

    private StringProperty screenshotDirPath = new SimpleStringProperty(Common.USER_DIR + File.separator + "screenshots");

    private BooleanProperty runEmulatorAtStartup = new SimpleBooleanProperty(false);

    private StringProperty themeId = new SimpleStringProperty(Common.THEME_DEFAULT_DARK);
    //</editor-fold>

    public GlcdConfigApp() {
    }

    //<editor-fold desc="Getter/Setters">
    public boolean isRunEmulatorAtStartup() {
        return runEmulatorAtStartup.get();
    }

    public BooleanProperty runEmulatorAtStartupProperty() {
        return runEmulatorAtStartup;
    }

    public void setRunEmulatorAtStartup(boolean runEmulatorAtStartup) {
        this.runEmulatorAtStartup.set(runEmulatorAtStartup);
    }

    public String getProfileDirPath() {
        return profileDirPath.get();
    }

    public StringProperty profileDirPathProperty() {
        return profileDirPath;
    }

    public void setProfileDirPath(String profileDirPath) {
        this.profileDirPath.set(profileDirPath);
    }

    public int getDefaultProfileId() {
        return defaultProfileId.get();
    }

    public IntegerProperty defaultProfileIdProperty() {
        return defaultProfileId;
    }

    public boolean isDefault(GlcdEmulatorProfile profile) {
        return defaultProfileId.get() == profile.getId();
    }

    public void setDefaultProfile(GlcdEmulatorProfile profile) {
        if (!isDefault(profile))
            setDefaultProfileId(profile.getId());
    }

    public void setDefaultProfileId(int defaultProfileId) {
        this.defaultProfileId.set(defaultProfileId);
    }

    public int getLastGeneratedProfileId() {
        return lastGeneratedProfileId.get();
    }

    public IntegerProperty lastGeneratedProfileIdProperty() {
        return lastGeneratedProfileId;
    }

    public void setLastGeneratedProfileId(int lastGeneratedProfileId) {
        this.lastGeneratedProfileId.set(lastGeneratedProfileId);
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop.get();
    }

    public BooleanProperty alwaysOnTopProperty() {
        return alwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        this.alwaysOnTop.set(alwaysOnTop);
    }

    public boolean isShowSettingsPane() {
        return showSettingsPane.get();
    }

    public BooleanProperty showSettingsPaneProperty() {
        return showSettingsPane;
    }

    public void setShowSettingsPane(boolean showSettingsPane) {
        this.showSettingsPane.set(showSettingsPane);
    }

    public boolean isShowPinActivityPane() {
        return showPinActivityPane.get();
    }

    public BooleanProperty showPinActivityPaneProperty() {
        return showPinActivityPane;
    }

    public void setShowPinActivityPane(boolean showPinActivityPane) {
        this.showPinActivityPane.set(showPinActivityPane);
    }

    public String getLastSavedImagePath() {
        return lastSavedImagePath.get();
    }

    public StringProperty lastSavedImagePathProperty() {
        return lastSavedImagePath;
    }

    public void setLastSavedImagePath(String lastSavedImagePath) {
        this.lastSavedImagePath.set(lastSavedImagePath);
    }

    public String getLastOpenFilePath() {
        return lastOpenFilePath.get();
    }

    public void setLastOpenFilePath(String lastOpenFilePath) {
        this.lastOpenFilePath.set(lastOpenFilePath);
    }

    public StringProperty lastOpenFilePathProperty() {
        return lastOpenFilePath;
    }

    public String getListenIp() {
        return listenIp.get();
    }

    public StringProperty listenIpProperty() {
        return listenIp;
    }

    public void setListenIp(String listenIp) {
        this.listenIp.set(listenIp);
    }

    public Integer getListenPort() {
        return listenPort.get();
    }

    public ObjectProperty<Integer> listenPortProperty() {
        return listenPort;
    }

    public void setListenPort(Integer listenPort) {
        this.listenPort.set(listenPort);
    }

    public double getPrefWindowWidth() {
        return prefWindowWidth.get();
    }

    public DoubleProperty prefWindowWidthProperty() {
        return prefWindowWidth;
    }

    public void setPrefWindowWidth(double prefWindowWidth) {
        this.prefWindowWidth.set(prefWindowWidth);
    }

    public double getPrefWindowHeight() {
        return prefWindowHeight.get();
    }

    public DoubleProperty prefWindowHeightProperty() {
        return prefWindowHeight;
    }

    public void setPrefWindowHeight(double prefWindowHeight) {
        this.prefWindowHeight.set(prefWindowHeight);
    }

    public double getMinWindowWidth() {
        return minWindowWidth.get();
    }

    public DoubleProperty minWindowWidthProperty() {
        return minWindowWidth;
    }

    public void setMinWindowWidth(double minWindowWidth) {
        this.minWindowWidth.set(minWindowWidth);
    }

    public double getMinWindowHeight() {
        return minWindowHeight.get();
    }

    public DoubleProperty minWindowHeightProperty() {
        return minWindowHeight;
    }

    public void setMinWindowHeight(double minWindowHeight) {
        this.minWindowHeight.set(minWindowHeight);
    }

    public boolean isConfirmOnExit() {
        return confirmOnExit.get();
    }

    public BooleanProperty confirmOnExitProperty() {
        return confirmOnExit;
    }

    public void setConfirmOnExit(boolean confirmOnExit) {
        this.confirmOnExit.set(confirmOnExit);
    }

    public boolean isMaximized() {
        return maximized.get();
    }

    public BooleanProperty maximizedProperty() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized.set(maximized);
    }

    public boolean isRememberSettingsOnExit() {
        return rememberSettingsOnExit.get();
    }

    public BooleanProperty rememberSettingsOnExitProperty() {
        return rememberSettingsOnExit;
    }

    public void setRememberSettingsOnExit(boolean rememberSettingsOnExit) {
        this.rememberSettingsOnExit.set(rememberSettingsOnExit);
    }

    public boolean isAutoFitWindowToScreen() {
        return autoFitWindowToScreen.get();
    }

    public SimpleBooleanProperty autoFitWindowToScreenProperty() {
        return autoFitWindowToScreen;
    }

    public void setAutoFitWindowToScreen(boolean autoFitWindowToScreen) {
        this.autoFitWindowToScreen.set(autoFitWindowToScreen);
    }

    public String getScreenshotDirPath() {
        return screenshotDirPath.get();
    }

    public StringProperty screenshotDirPathProperty() {
        return screenshotDirPath;
    }

    public void setScreenshotDirPath(String screenshotDirPath) {
        this.screenshotDirPath.set(screenshotDirPath);
    }

    public String getThemeId() {
        return themeId.get();
    }

    public StringProperty themeIdProperty() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId.set(themeId);
    }
    //</editor-fold>

    public int nextProfileId() {
        lastGeneratedProfileId.set(lastGeneratedProfileId.get() + 1);
        return lastGeneratedProfileId.get();
    }

    public boolean isToolbarVisible() {
        return toolbarVisible.get();
    }

    public BooleanProperty toolbarVisibleProperty() {
        return toolbarVisible;
    }

    public void setToolbarVisible(boolean toolbarVisible) {
        this.toolbarVisible.set(toolbarVisible);
    }
}
