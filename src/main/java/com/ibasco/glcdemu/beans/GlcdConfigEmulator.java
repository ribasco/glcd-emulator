package com.ibasco.glcdemu.beans;

import com.ibasco.glcdemu.annotations.NoCompare;
import com.ibasco.glcdemu.model.GlcdCommType;
import com.ibasco.glcdemu.model.GlcdControllerType;
import com.ibasco.glcdemu.utils.NetUtils;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;

abstract public class GlcdConfigEmulator extends GlcdConfigBase {

    private Color lcdBackground = Color.DODGERBLUE;

    private Color lcdForeground = Color.WHITE;

    private boolean alwaysOntop = false;

    private boolean showSettingsPane = false;

    private boolean showLogActivityPane = false;

    private boolean showPinActivityPane = false;

    @NoCompare
    private String lastSavedImagePath = System.getProperty("user.dir");

    @NoCompare
    private String lastOpenFilePath = System.getProperty("user.dir");

    private int displaySizeWidth = 128;

    private int displaySizeHeight = 64;

    private double pixelSize = 5.0;

    private GlcdControllerType emulatorController = GlcdControllerType.ST7920;

    private GlcdCommType emulatorCommType = GlcdCommType.SPI;

    private String listenIp = NetUtils.getLocalAddress("127.0.0.1");

    private int listenPort = 3580;

    private String themeId = "menuThemeDefault";

    private double prefWindowWidth = 1024;

    private double prefWindowHeight = 768;

    private double minWindowWidth = 100;

    private double minWindowHeight = 100;

    private boolean confirmOnExit = true;

    private boolean maximized = false;

    private boolean rememberSettingsOnExit = false;

    private boolean autoFitWindowToScreen = false;

    private String screenshotDirPath = System.getProperty("user.dir") + File.separator + "screenshots";

    public boolean isConfirmOnExit() {
        return confirmOnExit;
    }

    public void setConfirmOnExit(boolean confirmOnExit) {
        this.confirmOnExit = confirmOnExit;
    }

    public boolean isRememberSettingsOnExit() {
        return rememberSettingsOnExit;
    }

    public void setRememberSettingsOnExit(boolean rememberSettingsOnExit) {
        this.rememberSettingsOnExit = rememberSettingsOnExit;
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

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public Color getLcdBackground() {
        return lcdBackground;
    }

    public void setLcdBackground(Color lcdBackground) {
        this.lcdBackground = lcdBackground;
    }

    public Color getLcdForeground() {
        return lcdForeground;
    }

    public void setLcdForeground(Color lcdForeground) {
        this.lcdForeground = lcdForeground;
    }

    public boolean isAlwaysOntop() {
        return alwaysOntop;
    }

    public void setAlwaysOntop(boolean alwaysOntop) {
        this.alwaysOntop = alwaysOntop;
    }

    public boolean isShowSettingsPane() {
        return showSettingsPane;
    }

    public void setShowSettingsPane(boolean showSettingsPane) {
        this.showSettingsPane = showSettingsPane;
    }

    public boolean isShowLogActivityPane() {
        return showLogActivityPane;
    }

    public void setShowLogActivityPane(boolean showLogActivityPane) {
        this.showLogActivityPane = showLogActivityPane;
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

    public int getDisplaySizeWidth() {
        return displaySizeWidth;
    }

    public void setDisplaySizeWidth(int displaySizeWidth) {
        this.displaySizeWidth = displaySizeWidth;
    }

    public int getDisplaySizeHeight() {
        return displaySizeHeight;
    }

    public void setDisplaySizeHeight(int displaySizeHeight) {
        this.displaySizeHeight = displaySizeHeight;
    }

    public double getPixelSize() {
        return pixelSize;
    }

    public void setPixelSize(double pixelSize) {
        this.pixelSize = pixelSize;
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

    public GlcdControllerType getEmulatorController() {
        return emulatorController;
    }

    public void setEmulatorController(GlcdControllerType emulatorController) {
        this.emulatorController = emulatorController;
    }

    public GlcdCommType getEmulatorCommType() {
        return emulatorCommType;
    }

    public void setEmulatorCommType(GlcdCommType emulatorCommType) {
        this.emulatorCommType = emulatorCommType;
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

    public String getLastOpenFilePath() {
        return lastOpenFilePath;
    }

    public void setLastOpenFilePath(String lastOpenFilePath) {
        this.lastOpenFilePath = lastOpenFilePath;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public boolean isAutoFitWindowToScreen() {
        return autoFitWindowToScreen;
    }

    public void setAutoFitWindowToScreen(boolean autoFitWindowToScreen) {
        this.autoFitWindowToScreen = autoFitWindowToScreen;
    }

    public String getScreenshotDirPath() {
        return screenshotDirPath;
    }

    public void setScreenshotDirPath(String screenshotDirPath) {
        this.screenshotDirPath = screenshotDirPath;
    }
}
