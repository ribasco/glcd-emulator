/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: Stages.java
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
package com.ibasco.glcdemulator;

import com.ibasco.glcdemulator.constants.Views;
import com.ibasco.glcdemulator.utils.ResourceUtil;
import com.jfoenix.controls.JFXDrawersStack;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class Stages {
    private static Stage fontBrowserStage;

    private static Stage primaryStage;

    private static Stage editProfileStage;

    private static Stage aboutStage;

    private static final Logger log = LoggerFactory.getLogger(Stages.class);

    private static Stage developerStage;

    public static Stage getDeveloperStage() {
        if (developerStage == null) {
            developerStage = StageHelper.createDialog(getPrimaryStage(), "Developer Window", Views.DEVELOPER_WINDOW, Modality.NONE);
            developerStage.getIcons().clear();
            developerStage.getIcons().add(new Image(ResourceUtil.getResource("images/developer.png").toExternalForm()));
        }
        return developerStage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Stage getAboutStage() {
        if (aboutStage == null) {
            aboutStage = com.ibasco.glcdemulator.StageHelper.createDialog(getPrimaryStage(), "About", Views.ABOUT_DIALOG);
        }
        return aboutStage;
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
                    "GLCD Font Browser",
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
