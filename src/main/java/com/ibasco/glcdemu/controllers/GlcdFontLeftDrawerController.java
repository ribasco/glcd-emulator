package com.ibasco.glcdemu.controllers;

import com.ibasco.glcdemu.GlcdController;
import com.ibasco.glcdemu.model.FontCacheDetails;
import com.ibasco.glcdemu.model.FontCacheEntry;
import com.ibasco.glcdemu.services.FontCacheService;
import com.jfoenix.controls.JFXListView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class GlcdFontLeftDrawerController extends GlcdController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GlcdFontLeftDrawerController.class);

    @FXML
    private JFXListView<FontCacheEntry> lvFonts;

    private final FontCacheService fontCacheService;

    private final FontCacheDetails details;

    public GlcdFontLeftDrawerController(FontCacheService service, FontCacheDetails details) {
        this.fontCacheService = service;
        this.details = details;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvFonts.setCellFactory(param -> new ListCell<FontCacheEntry>() {
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
        });

        ObjectBinding<ObservableList<FontCacheEntry>> cacheListBinding = Bindings.createObjectBinding(() -> {
            if (fontCacheService.getValue() == null)
                return FXCollections.observableArrayList();
            return fontCacheService.getValue();
        }, fontCacheService.valueProperty());
        lvFonts.itemsProperty().bind(cacheListBinding);

        details.activeEntryProperty().bind(lvFonts.getSelectionModel().selectedItemProperty());
    }
}
