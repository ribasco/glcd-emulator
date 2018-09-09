package com.ibasco.glcdemu.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FontCacheDetails {
    private StringProperty previewText = new SimpleStringProperty();

    private ObjectProperty<FontCacheEntry> activeEntry = new SimpleObjectProperty<>();

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


}
