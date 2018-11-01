/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: Bootstrap.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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
package com.ibasco.glcdemu;

import com.ibasco.glcdemu.constants.Views;
import com.ibasco.glcdemu.model.GlcdConfigApp;
import com.ibasco.glcdemu.utils.DialogUtil;
import static com.ibasco.glcdemu.utils.DialogUtil.REPORT_BUTTON;
import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main application bootstrap
 *
 * @author Rafael Ibasco
 */
public class Bootstrap extends Application {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);

    private static final String APP_TITLE = "GLCD Emulator - Alpha";

    private GlcdConfigApp appConfig;

    private static final double MIN_STAGE_WIDTH_SETTINGS = 1135.0;

    private static final double MIN_STAGE_WIDTH_DEFAULT = 100.0;

    private static final int MAX_REPEATED_ERRORS = 2;

    private static AtomicInteger errorCount = new AtomicInteger(1);

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stages.setPrimaryStage(primaryStage);
        Context.getInstance().setHostServices(this.getHostServices());

        this.appConfig = Context.getInstance().getAppConfig();
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            if (errorCount.incrementAndGet() > MAX_REPEATED_ERRORS) {
                return;
            }
            log.error("Uncaught system exception occured", e);
            Platform.runLater(() -> {
                Alert dialog = DialogUtil.createExceptionDialog("Ooops, an unexpected error has occured!", e.toString(), e);
                try {
                    Optional<ButtonType> result = dialog.showAndWait();
                    if (result.isPresent() && REPORT_BUTTON.equals(result.get())) {
                        //TODO: Implement report to Github
                        log.debug("Reporting Issue to Github...");
                    }
                } finally {
                    errorCount.decrementAndGet();
                }
            });
        });
        launchEmulator();
    }

    private void launchEmulator() throws IOException {
        initStageProperties();
        initStageBindings();
        Parent root = ResourceUtil.loadFxmlResource(Views.EMULATOR, null);
        Controllers.setEmulatorController(ResourceUtil.getLastController());
        if (root == null)
            throw new IOException("Could not load primary view");
        Stage stage = Context.getPrimaryStage();
        Scene scene = new Scene(root);
        Context.getInstance().getThemeManager().applyTheme(scene);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    private void initStageBindings() {
        Stage stage = Stages.getPrimaryStage();
        stage.getIcons().add(new Image(ResourceUtil.getResource("images/icon.png").toExternalForm()));
        appConfig.alwaysOnTopProperty().addListener((observable, oldValue, newValue) -> stage.setAlwaysOnTop(newValue));
        appConfig.maximizedProperty().bind(stage.maximizedProperty());
        appConfig.prefWindowWidthProperty().bind(stage.widthProperty());
        appConfig.prefWindowHeightProperty().bind(stage.heightProperty());
        stage.minWidthProperty().bind(Bindings.createDoubleBinding(() -> appConfig.isShowSettingsPane() ? MIN_STAGE_WIDTH_SETTINGS : MIN_STAGE_WIDTH_DEFAULT, appConfig.showSettingsPaneProperty()));
        stage.minHeightProperty().bindBidirectional(appConfig.minWindowHeightProperty());
    }

    private void initStageProperties() {
        Stage stage = Stages.getPrimaryStage();

        //Initialize Stage Properties
        stage.setTitle(APP_TITLE);
        stage.setIconified(false);
        stage.setAlwaysOnTop(appConfig.isAlwaysOnTop());
        stage.setMinWidth(appConfig.getMinWindowWidth());
        stage.setMinHeight(appConfig.getMinWindowHeight());
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::handleWindowCloseRequests);

        if (appConfig.isMaximized())
            stage.setMaximized(appConfig.isMaximized());
        else {
            stage.setWidth(appConfig.getPrefWindowWidth());
            stage.setHeight(appConfig.getPrefWindowHeight());
        }
    }

    private void handleWindowCloseRequests(WindowEvent event) {
        GlcdConfigApp settings = Context.getInstance().getAppConfig();

        if (!settings.isConfirmOnExit()) {
            return;
        }

        Alert closeConfirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Confirm action", ButtonType.YES, ButtonType.NO
        );
        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(ButtonType.YES);
        Button cancelButton = (Button) closeConfirmation.getDialogPane().lookupButton(ButtonType.NO);
        exitButton.setDefaultButton(false);
        cancelButton.setDefaultButton(true);

        closeConfirmation.setHeaderText("Are you sure you want to exit?");
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        closeConfirmation.initOwner(Stages.getPrimaryStage());

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();

        if (closeResponse.isPresent() && ButtonType.NO.equals(closeResponse.get())) {
            event.consume();
        }
    }

    public static void main(String[] args) {
        System.setProperty("quantum.multithreaded", "false");
        launch(args);
    }
}
