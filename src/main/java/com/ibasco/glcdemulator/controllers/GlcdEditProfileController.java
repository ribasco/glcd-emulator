/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdEditProfileController.java
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
import com.ibasco.glcdemulator.model.GlcdEmulatorProfile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class GlcdEditProfileController extends Controller implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GlcdEditProfileController.class);

    @FXML
    private TextField tfProfileName;

    @FXML
    private TextArea tfProfileDescription;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    private GlcdEmulatorProfile originalCopy;

    private Stage owner;

    private ObjectProperty<GlcdEmulatorProfile> profile = new SimpleObjectProperty<GlcdEmulatorProfile>() {
        @Override
        protected void invalidated() {
            GlcdEditProfileController.this.originalCopy = new GlcdEmulatorProfile(get());
            tfProfileName.textProperty().bindBidirectional(get().nameProperty());
            tfProfileDescription.textProperty().bindBidirectional(get().descriptionProperty());
        }
    };

    public GlcdEditProfileController() {

    }

    private void undoChanges() {
        getProfile().setName(originalCopy.getName());
        getProfile().setDescription(originalCopy.getDescription());
    }

    public GlcdEmulatorProfile getProfile() {
        return profile.get();
    }

    public ObjectProperty<GlcdEmulatorProfile> profileProperty() {
        return profile;
    }

    public void setProfile(GlcdEmulatorProfile profile) {
        this.profile.set(profile);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventHandler<KeyEvent> handleKeyPress = event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!event.isControlDown()) {
                    btnSave.fire();
                    event.consume();
                }
            }
        };

        tfProfileName.addEventFilter(KeyEvent.KEY_RELEASED, handleKeyPress);
        tfProfileDescription.addEventFilter(KeyEvent.KEY_RELEASED, handleKeyPress);
        btnSave.setOnAction(event -> owner.close());
        btnCancel.setOnAction(event -> {
            log.debug("Closing");
            undoChanges();
            tfProfileName.textProperty().unbindBidirectional(getProfile().nameProperty());
            tfProfileDescription.textProperty().unbindBidirectional(getProfile().descriptionProperty());
            owner.close();
        });
    }

    public void setOwner(Stage owner) {
        this.owner = owner;

    }
}
