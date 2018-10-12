package com.ibasco.glcdemu.services;

import com.google.gson.reflect.TypeToken;
import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.constants.Common;
import com.ibasco.glcdemu.controls.GlcdScreen;
import com.ibasco.glcdemu.enums.PixelShape;
import com.ibasco.glcdemu.model.FontCacheEntry;
import com.ibasco.glcdemu.utils.*;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdFont;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * A service that cache's font details (thumbnail previews, width, height etc)
 *
 * @author Rafael Ibasco
 */
public class FontCacheService extends Service<ObservableList<FontCacheEntry>> {

    private static final Logger log = LoggerFactory.getLogger(FontCacheService.class);

    private StringProperty cacheDirPath = new SimpleStringProperty(Common.FONT_CACHE_DIR_PATH);

    private StringProperty cacheFilePath = new SimpleStringProperty(Common.FONT_CACHE_FILE_PATH);

    private GlcdScreen screenBuffer;

    private FontRenderer renderer;

    public FontCacheService() {
        setExecutor(Context.getTaskExecutor());
        renderer = FontRenderer.getInstance();
        screenBuffer = createFontDisplay();
    }

    @Override
    protected Task<ObservableList<FontCacheEntry>> createTask() {
        return new Task<ObservableList<FontCacheEntry>>() {
            @Override
            protected ObservableList<FontCacheEntry> call() throws Exception {
                ArrayList<FontCacheEntry> entries;

                //Check if we have existing cached entries
                entries = loadCacheEntries();

                if (entries != null) {
                    log.debug("Font Cache Service: Found existing cached entries (Size: {})", entries.size());
                    return FXCollections.observableArrayList(entries);
                }

                //Load from cache
                entries = new ArrayList<>();

                FileUtils.ensureDirectoryExistence(cacheDirPath.get());
                GlcdFont[] values = GlcdFont.values();

                log.info("Font Cache Service: Processing a total of {} fonts", values.length);

                for (int i = 0; i < values.length; i++) {
                    GlcdFont font = values[i];
                    try {
                        FontRenderer.FontInfo info = renderer.getFontInfo(font);
                        File cachedImagePath = createImageFile(font);

                        //Render a font with the provided text
                        renderer.renderFont(screenBuffer, font, createPreviewText(font));

                        double width = screenBuffer.getWidth(), height = screenBuffer.getHeight();

                        //Check if anything has been drawn to the display
                        if (screenBuffer.getBuffer().isEmpty())
                            drawNotAvailableScreen(width, height);
                        else
                            screenBuffer.refresh();

                        NodeUtil.saveNodeImageToFile(screenBuffer, cachedImagePath, width, height);
                        FontCacheEntry entry = new FontCacheEntry(info.getAscent(), info.getDescent(), font, cachedImagePath);
                        entries.add(entry);
                    } catch (Exception e) {
                        log.warn("Font Cache Service: Unable to cache font '" + font.name() + "' (Reason: {})", e.toString());
                    } finally {
                        updateProgress(i, values.length);
                    }
                }

                log.info("Font Cache Service: Cache generation complete");

                if (!entries.isEmpty()) {
                    saveCacheEntries(entries);
                }
                return FXCollections.observableArrayList(entries);
            }
        };
    }

    /**
     * Rebuild the cache
     */
    public void rebuild() {
        if (isRunning())
            return;
        try {
            //Delete cache directory
            File cacheDir = new File(cacheDirPath.get());
            if (cacheDir.exists()) {
                log.debug("Rebuild: Removing existing cache entries");
                org.apache.commons.io.FileUtils.deleteDirectory(cacheDir);
            }
            log.debug("Rebuild: Cache successfully deleted. Now rebuilding cache");
            restart();
        } catch (IOException e) {
            log.debug("Rebuild: Problem occured while trying to rebuild cache", e);
        }
    }

    @Override
    protected void failed() {
        log.error("Font cache service has failed", getException());
        super.failed();
    }

    private GlcdScreen createFontDisplay() {
        GlcdScreen display = new GlcdScreen(false);
        display.setBuffer(new PixelBuffer(renderer.getDriver().getWidth(), renderer.getDriver().getHeight()));
        display.setGradientBacklight(false);
        display.setBacklightColor(Color.web("#212121")); //79ff4d
        display.setActivePixelColor(Color.LIGHTGRAY);
        display.setInactivePixelColor(Color.LIGHTGRAY);
        display.setDropShadowVisible(false);
        display.setContrast(6.59f);
        display.setPixelSize(1.81f);
        display.setSpacing(0.0);
        display.setMargin(5.97f);
        display.setPixelShape(PixelShape.RECTANGLE);
        return display;
    }

    private void drawNotAvailableScreen(double width, double height) {
        GraphicsContext gc = screenBuffer.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFont(new Font("Verdana", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(screenBuffer.getBacklightColor().darker());
        gc.fillRect(0, 0, width, height);
        gc.setFill(screenBuffer.getBacklightColor().invert());
        gc.fillText("Preview Not Available", 120.0, 60.0);
    }

    private String createPreviewText(GlcdFont font) {
        String previewText = "ABC";
        if (font.name().toLowerCase().endsWith("n"))
            previewText = "01234";
        return previewText;
    }

    private File createImageFile(GlcdFont font) {
        return new File(cacheDirPath.get() + File.separator + font.name() + ".png");
    }

    private ArrayList<FontCacheEntry> loadCacheEntries() {
        try {
            File cacheFile = new File(cacheFilePath.get());
            if (cacheFile.exists()) {
                String json = org.apache.commons.io.FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
                Type fooType = new TypeToken<ArrayList<FontCacheEntry>>() {
                }.getType();
                return JsonUtils.fromJson(json, fooType);
            }
        } catch (IOException e) {
            log.error("Error during cache-read operation", e);
        }
        return null;
    }

    private void saveCacheEntries(ArrayList<FontCacheEntry> entries) {
        try {
            String json = JsonUtils.toJson(entries);
            File cacheFile = new File(cacheFilePath.get());
            org.apache.commons.io.FileUtils.writeStringToFile(cacheFile, json, StandardCharsets.UTF_8, false);
            log.info("Font Cache Service: Saved cache details to {}", cacheFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error during cache-write operation", e);
        }
    }

    public String getCacheFilePath() {
        return cacheFilePath.get();
    }

    public StringProperty cacheFilePathProperty() {
        return cacheFilePath;
    }

    public void setCacheFilePath(String cacheFilePath) {
        this.cacheFilePath.set(cacheFilePath);
    }

    public String getCacheDirPath() {
        return cacheDirPath.get();
    }

    public StringProperty cacheDirPathProperty() {
        return cacheDirPath;
    }

    public void setCacheDirPath(String cacheDirPath) {
        this.cacheDirPath.set(cacheDirPath);
    }
}
