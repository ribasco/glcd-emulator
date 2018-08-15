package com.ibasco.glcdemu;

import com.ibasco.glcdemu.beans.GlcdConfigEmulatorProfile;
import com.ibasco.glcdemu.services.GlcdConfigService;
import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private int columns = 128;
    private int rows = 64;
    private double width = 800;
    private double height = 600;

    private GlcdController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        //launchEditableGrid(primaryStage);
        launchEmulator(primaryStage);
    }

    private void launchEmulator(Stage stage) throws IOException {

        GlcdConfigEmulatorProfile settings = GlcdConfigService.getProfileManager().getActiveProfile();

        if (settings.isMaximized()) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(settings.getPrefWindowWidth());
            stage.setHeight(settings.getPrefWindowHeight());
            stage.setMinWidth(settings.getMinWindowWidth());
            stage.setMinHeight(settings.getMinWindowHeight());
        }

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> settings.setMaximized(newValue));
        stage.setOnCloseRequest(confirmCloseEventHandler(stage));

        FXMLLoader loader = new FXMLLoader(ResourceUtil.getFxmlResource("emulator"));

        loader.setClassLoader(getClass().getClassLoader());

        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("GLCD Emulator");
        stage.setScene(scene);

        controller.onInit();
        stage.show();
    }

    private EventHandler<WindowEvent> confirmCloseEventHandler(Stage stage) {
        return windowEvent -> {
            GlcdConfigEmulatorProfile settings = GlcdConfigService.getProfileManager().getActiveProfile();

            if (!settings.isConfirmOnExit()) {
                controller.onClose();
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
            closeConfirmation.initOwner(stage);

            Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
            if (!ButtonType.YES.equals(closeResponse.get())) {
                windowEvent.consume();
            } else {
                controller.onClose();
            }
        };
    }

    private void launchEditableGrid(Stage primaryStage) {
        try {
            primaryStage.setWidth(10 * columns);
            primaryStage.setHeight(10 * rows);

            StackPane root = new StackPane();
            MouseGestures mg = new MouseGestures();
            Grid grid = new Grid(columns, rows, width, height);

            // fill grid
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    Cell cell = new Cell(column, row);
                    mg.makePaintable(cell);
                    grid.add(cell, column, row);
                }
            }

            root.getChildren().addAll(grid);

            // create scene and stage
            Scene scene = new Scene(root, width, height);
            URL appStyle = ResourceUtil.getStylesheet("app.css");

            scene.getStylesheets().add(appStyle.toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
