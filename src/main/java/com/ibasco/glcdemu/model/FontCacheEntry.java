package com.ibasco.glcdemu.model;

import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.File;

public class FontCacheEntry {
    private ReadOnlyIntegerWrapper ascent = new ReadOnlyIntegerWrapper();

    private ReadOnlyIntegerWrapper descent = new ReadOnlyIntegerWrapper();

    private ReadOnlyObjectWrapper<GlcdFont> font = new ReadOnlyObjectWrapper<>();

    private ReadOnlyObjectWrapper<File> image = new ReadOnlyObjectWrapper<>();

    public FontCacheEntry(int ascent, int descent, GlcdFont font, File cachedImage) {
        this.ascent.set(ascent);
        this.descent.set(descent);
        this.font.set(font);
        this.image.set(cachedImage);
    }

    public File getImage() {
        return image.get();
    }

    public ReadOnlyObjectWrapper<File> imageProperty() {
        return image;
    }

    public int getAscent() {
        return ascent.get();
    }

    public ReadOnlyIntegerProperty ascentProperty() {
        return ascent.getReadOnlyProperty();
    }

    public int getDescent() {
        return descent.get();
    }

    public ReadOnlyIntegerProperty descentProperty() {
        return descent.getReadOnlyProperty();
    }

    public GlcdFont getFont() {
        return font.get();
    }

    public ReadOnlyObjectProperty<GlcdFont> fontProperty() {
        return font.getReadOnlyProperty();
    }


}
