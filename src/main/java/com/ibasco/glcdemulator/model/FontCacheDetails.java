/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: FontCacheDetails.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 - 2019 Rafael Luis Ibasco
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

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class FontCacheDetails {
    private StringProperty previewText = new SimpleStringProperty();

    private ListProperty<FontCacheEntry> entries = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ObjectProperty<FontCacheEntry> activeEntry = new SimpleObjectProperty<>();

    private ObjectProperty<FilteredList<FontCacheEntry>> filteredEntries = new SimpleObjectProperty<>(new FilteredList<>(entries.get()));

    public String getPreviewText() {
        return previewText.get();
    }

    public StringProperty previewTextProperty() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText.set(previewText);
    }

    public FontCacheEntry getActiveEntry() {
        return activeEntry.get();
    }

    public ObjectProperty<FontCacheEntry> activeEntryProperty() {
        return activeEntry;
    }

    public void setActiveEntry(FontCacheEntry activeEntry) {
        this.activeEntry.set(activeEntry);
    }

    public ObservableList<FontCacheEntry> getEntries() {
        return entries.get();
    }

    public ListProperty<FontCacheEntry> entriesProperty() {
        return entries;
    }

    public void setEntries(ObservableList<FontCacheEntry> entries) {
        this.entries.set(entries);
    }

    public FilteredList<FontCacheEntry> getFilteredEntries() {
        return filteredEntries.get();
    }

    public ObjectProperty<FilteredList<FontCacheEntry>> filteredEntriesProperty() {
        return filteredEntries;
    }

    public void setFilteredEntries(FilteredList<FontCacheEntry> filteredEntries) {
        this.filteredEntries.set(filteredEntries);
    }
}
