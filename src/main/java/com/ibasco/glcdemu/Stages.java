package com.ibasco.glcdemu;

import com.ibasco.glcdemu.constants.Views;
import com.jfoenix.controls.JFXDrawersStack;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class Stages {
    private static Stage fontBrowserStage;

    private static Stage primaryStage;

    private static Stage editProfileStage;

    private static final Logger log = LoggerFactory.getLogger(Stages.class);

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Stage getEditProfileStage() {
        if (editProfileStage == null) {
            editProfileStage = StageHelper.createDialog(getPrimaryStage(), "Edit Profile", Views.EDIT_PROFILE_DIALOG, Controllers.getEditProfileController());
        }
        return editProfileStage;
    }

    public static Stage getFontBrowserStage() {
        if (fontBrowserStage == null) {
            JFXDrawersStack drawersStack = new JFXDrawersStack();
            Scene scene = new Scene(drawersStack);
            fontBrowserStage = StageHelper.createDialog(Context.getPrimaryStage(),
                    "U8G2 Font Browser",
                    Views.FONT_BROWSER_DIALOG,
                    Controllers.getFontBrowserController(),
                    scene,
                    (Consumer<VBox>) drawersStack::setContent
            );
        }
        return fontBrowserStage;
    }

    static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
}
