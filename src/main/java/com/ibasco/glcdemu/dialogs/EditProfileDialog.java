package com.ibasco.glcdemu.dialogs;

import com.ibasco.glcdemu.model.GlcdEmulatorProfile;
import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class EditProfileDialog extends Dialog<GlcdEmulatorProfile> implements Initializable  {

    private static final Logger log = LoggerFactory.getLogger(EditProfileDialog.class);

    private GlcdEmulatorProfile profile;

    @FXML
    private TextField tfProfileName;

    @FXML
    private TextArea tfProfileDescription;

    public EditProfileDialog() {
        final DialogPane dialogPane = getDialogPane();

        try {
            Parent resource = ResourceUtil.loadFxmlResource("edit-profile-dialog");
            dialogPane.getButtonTypes().add(new ButtonType("Save", ButtonBar.ButtonData.OK_DONE));
            dialogPane.getButtonTypes().add(ButtonType.CANCEL);
            dialogPane.setContent(resource);
            setResultConverter((dialogButton) -> {
                ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
                return data == ButtonBar.ButtonData.OK_DONE ? null : null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Edit profile dialog initialized");
    }

    public GlcdEmulatorProfile getProfile() {
        return profile;
    }

    public void setProfile(GlcdEmulatorProfile profile) {
        this.profile = profile;
    }

    private void updateGrid() {

    }
}
