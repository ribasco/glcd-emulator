/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: Bootstrap.java
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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.ibasco.glcdemulator.constants.Common;
import com.ibasco.glcdemulator.constants.Views;
import com.ibasco.glcdemulator.model.GlcdConfigApp;
import com.ibasco.glcdemulator.utils.DialogUtil;
import static com.ibasco.glcdemulator.utils.DialogUtil.REPORT_BUTTON;
import com.ibasco.glcdemulator.utils.ResourceUtil;
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
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
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

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Bootstrap.class);

    private static final String APP_TITLE = "GLCD Emulator - " + Context.getAppVersion();

    private static final String OPT_DEV_SHORT = "dev";

    private static final String OPT_DEV_LONG = "developer";

    private static final String OPT_LIMITFPS_SHORT = "l";

    private static final String OPT_LIMITFPS_LONG = "limitFps";

    private static final String OPT_LOGLEVEL_SHORT = "log";

    private static final String OPT_LOGLEVEL_LONG = "logLevel";

    private static GlcdConfigApp appConfig;

    private static final double MIN_STAGE_WIDTH_SETTINGS = 1135.0;

    private static final double MIN_STAGE_WIDTH_DEFAULT = 100.0;

    private static final int MAX_REPEATED_ERRORS = 2;

    private static AtomicInteger errorCount = new AtomicInteger(1);

    private static final CommandLineParser parser = new DefaultParser();

    private static final HelpFormatter helpFormatter = new HelpFormatter();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stages.setPrimaryStage(primaryStage);
        Context.getInstance().setHostServices(this.getHostServices());
        log.info("App Version: {}", Context.getAppVersion());

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
                        Context.getInstance().getHostServices().showDocument(Common.REPORT_ISSUE_URL);
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
        Parent root = ResourceUtil.loadFxmlResource(Views.EMULATOR);
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
        stage.setTitle(APP_TITLE + (appConfig.isDeveloperMode() ? " (Developer Mode)" : ""));
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

    private static void initializeCLI(String[] args) {
        // create Options object
        Options options = new Options();
        // add t option
        options.addOption(OPT_DEV_SHORT, OPT_DEV_LONG, false, "Enable developer mode");
        options.addOption(OPT_LIMITFPS_SHORT, OPT_LIMITFPS_LONG, true, "On some operating systems, enabling this option would cap the screen FPS to 60");
        options.addOption(OPT_LOGLEVEL_SHORT, OPT_LOGLEVEL_LONG, true, "Sets the logging level of the application");

        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println();
            helpFormatter.printHelp("<glcd app> <options>", options);
            System.out.println();
            log.error("Command line parse exception", e);
            System.exit(-1);
        }

        if (cmd != null) {
            if (cmd.hasOption(OPT_DEV_SHORT)) {
                logPropValue("Developer Mode", true);
                appConfig.setDeveloperMode(true);
            } else {
                logPropValue("Developer Mode", false);
                appConfig.setDeveloperMode(false);
            }

            //Set system properties
            String ans = "false";
            if (cmd.hasOption(OPT_LIMITFPS_SHORT)) {
                ans = cmd.getOptionValue(OPT_LIMITFPS_SHORT, "false");
                if (ans.trim().equalsIgnoreCase("true")) {
                    logPropValue("Limit FPS", true);
                } else {
                    logPropValue("Limit FPS", false);
                }
            } else {
                logPropValue("Limit FPS", false);
            }
            System.setProperty("quantum.multithreaded", ans);

            //Set logging level
            if (cmd.hasOption(OPT_LOGLEVEL_SHORT)) {
                String logLevel = cmd.getOptionValue(OPT_LOGLEVEL_SHORT, "info").trim().toUpperCase();
                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                log.info("");
                for (Logger logger : loggerContext.getLoggerList()) {
                    Level level = Level.valueOf(logLevel);
                    log.info("> Setting '{}' Logger level to {}", logger, level);
                    logger.setLevel(level);
                }
            } else {
                logPropValue("Custom log level", false);
            }

            log.info("");
        }
    }

    private static void logPropValue(String name, boolean value) {
        log.info("> {}: {}", StringUtils.rightPad(name, 20), value ? "YES" : "NO");
    }

    public static void main(String[] args) throws Exception {
        log.info("..######...##........######..########.....########.##.....##.##.....##.##..........###....########..#######..########.");
        log.info(".##....##..##.......##....##.##.....##....##.......###...###.##.....##.##.........##.##......##....##.....##.##.....##");
        log.info(".##........##.......##.......##.....##....##.......####.####.##.....##.##........##...##.....##....##.....##.##.....##");
        log.info(".##...####.##.......##.......##.....##....######...##.###.##.##.....##.##.......##.....##....##....##.....##.########.");
        log.info(".##....##..##.......##.......##.....##....##.......##.....##.##.....##.##.......#########....##....##.....##.##...##..");
        log.info(".##....##..##.......##....##.##.....##....##.......##.....##.##.....##.##.......##.....##....##....##.....##.##....##.");
        log.info("..######...########..######..########.....########.##.....##..#######..########.##.....##....##.....#######..##.....##");
        log.info("");
        log.info("RUNTIME PROPERTIES");

        appConfig = Context.getInstance().getAppConfig();
        initializeCLI(args);
        launch(args);
    }
}
