/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdFontTopDrawerController.java
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
package com.ibasco.glcdemulator.controllers;

import com.ibasco.glcdemulator.Controller;
import com.ibasco.glcdemulator.model.FontCacheDetails;
import com.ibasco.glcdemulator.model.FontCacheEntry;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class GlcdFontTopDrawerController extends Controller implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GlcdFontTopDrawerController.class);

    @FXML
    private JFXListView<FontCacheEntry> lvFonts;

    private final FontCacheService fontCacheService;

    private final FontCacheDetails details;

    public GlcdFontTopDrawerController(FontCacheService service, FontCacheDetails details) {
        this.fontCacheService = service;
        this.details = details;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvFonts.setOrientation(Orientation.HORIZONTAL);
        lvFonts.setCellFactory(this::listCellFactory);
        details.activeEntryProperty().bind(lvFonts.getSelectionModel().selectedItemProperty());

        details.entriesProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty())
                lvFonts.getSelectionModel().selectFirst();
        });

        lvFonts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                lvFonts.scrollTo(newValue);
        });
        lvFonts.itemsProperty().bind(details.filteredEntriesProperty());
    }

    void selectFirst() {
        lvFonts.getSelectionModel().selectFirst();
    }

    void selectNextFont() {
        lvFonts.getSelectionModel().selectNext();
    }

    void selectPreviousFont() {
        lvFonts.getSelectionModel().selectPrevious();
    }

    private ListCell<FontCacheEntry> listCellFactory(ListView<FontCacheEntry> fontCacheEntryListView) {
        return new ListCell<FontCacheEntry>() {
            @Override
            public void updateItem(FontCacheEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Image image = new Image(entry.getImage().toURI().toString());
                    setTooltip(new Tooltip(entry.getFont().name()));
                    setGraphic(new ImageView(image));
                }
            }
        };
    }
}
