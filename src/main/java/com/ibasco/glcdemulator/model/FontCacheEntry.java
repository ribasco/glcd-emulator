/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: FontCacheEntry.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Luis Ibasco
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * =========================END==================================
 */
package com.ibasco.glcdemulator.model;

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
