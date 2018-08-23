package com.ibasco.glcdemu.domain;

import javafx.scene.paint.Color;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;

@Deprecated
public class GlcdConfigEmulator extends GlcdConfigCommon {

    private Color lcdBackground = Color.DODGERBLUE;

    private Color lcdForeground = Color.WHITE;

    private String screenshotDirPath = System.getProperty("user.dir") + File.separator + "screenshots";

    private int displaySizeWidth = 128;

    private int displaySizeHeight = 64;

    private double pixelSize = 5.0;

    private GlcdControllerType emulatorController = GlcdControllerType.ST7920;

    private GlcdCommType emulatorCommType = GlcdCommType.SPI;

    private String themeId = "menuThemeDefault";

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public String getScreenshotDirPath() {
        return screenshotDirPath;
    }

    public void setScreenshotDirPath(String screenshotDirPath) {
        this.screenshotDirPath = screenshotDirPath;
    }
}
