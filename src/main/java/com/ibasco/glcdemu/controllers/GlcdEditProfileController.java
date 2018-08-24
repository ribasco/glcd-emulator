package com.ibasco.glcdemu.controllers;

import com.ibasco.glcdemu.GlcdController;
import com.ibasco.glcdemu.model.GlcdEmulatorProfile;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.EventFilter;
import javax.xml.stream.events.XMLEvent;

public class GlcdEditProfileController extends GlcdController {

    private static final Logger log = LoggerFactory.getLogger(GlcdEditProfileController.class);

    @FXML
    private TextField tfProfileName;

    @FXML
    private TextArea tfProfileDescription;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    private GlcdEmulatorProfile profile;

    private GlcdEmulatorProfile originalCopy;

    public GlcdEditProfileController(Stage stage) {
        super(stage);
        stage.setOnCloseRequest(event -> {
            log.debug("Closing edit dialog");
            tfProfileName.textProperty().unbindBidirectional(profile.nameProperty());
            tfProfileDescription.textProperty().unbindBidirectional(profile.descriptionProperty());
            undoChanges();

        });
        stage.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    undoChanges();
                    stage.close();
                }
            }
        });
    }

    public void undoChanges() {
        profile.setName(originalCopy.getName());
        profile.setDescription(originalCopy.getDescription());
    }

    @Override
    public void onInit() {
        if (profile == null)
            return;

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
        btnSave.setOnAction(event -> getStage().close());
        btnCancel.setOnAction(event -> {
            undoChanges();
            getStage().close();
        });
    }

    @Override
    public boolean onClose() {
        return true;
    }

    public GlcdEmulatorProfile getProfile() {
        return profile;
    }

    public void setProfile(GlcdEmulatorProfile profile) {
        this.profile = profile;
        this.originalCopy = new GlcdEmulatorProfile(profile);
        tfProfileName.textProperty().bindBidirectional(profile.nameProperty());
        tfProfileDescription.textProperty().bindBidirectional(profile.descriptionProperty());
    }
}
