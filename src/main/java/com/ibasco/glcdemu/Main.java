package com.ibasco.glcdemu;

import com.ibasco.glcdemu.model.GlcdConfigApp;
import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Optional;

public class Main extends Application {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Callback<Class<?>, Object> controllerFactory;

    private GlcdController controller;

    private static final String APP_TITLE = "GLCD Emulator";

    private FXMLLoader loader;

    private Stage stage;

    private GlcdConfigApp appConfig;

    private static final double MIN_STAGE_WIDTH_SETTINGS = 848.0;

    private static final double MIN_STAGE_WIDTH_DEFAULT = 100.0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        //launchEditableGrid(primaryStage);
        this.stage = primaryStage;
        this.appConfig = Context.getInstance().getAppConfig();
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            log.error("Uncaught system exception occured", e);
            Platform.runLater(() -> {
                Alert dialog = createExceptionDialog(e);
                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent() && result.get().equals(reportType)) {
                    //TODO: Implement report to Github
                    log.debug("Reporting Issue to Github...");
                }
            });
        });
        launchEmulator();
    }

    private ButtonType reportType = new ButtonType("Report Issue", ButtonBar.ButtonData.CANCEL_CLOSE);

    private Alert createExceptionDialog(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK, reportType);
        alert.setTitle("Ooops, an unexpected error has occured!");
        alert.setHeaderText(ex.getMessage());
        alert.setContentText(ex.getCause() != null ? "Cause: " + ex.getCause().getMessage() : "");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setStyle("-fx-font-family: \"Courier New\"; -fx-font-size: 12px");
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);

        return alert;
    }

    private Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        if (controllerFactory == null) {
            controllerFactory = param -> {
                try {
                    return param.getConstructor(Stage.class).newInstance(stage);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException("Unable to produce controller", e);
                }
            };
        }
        return controllerFactory;
    }

    private void launchEmulator() throws IOException {
        initStageProperties(stage);
        initStageBindings(stage);

        Parent root = loadFXMLResource("emulator");
        controller = loader.getController();
        stage.setScene(new Scene(root));
        controller.onInit();
        stage.show();
        stage.toFront();
    }

    private Parent loadFXMLResource(String resource) throws IOException {
        FXMLLoader loader = getLoader();
        loader.setLocation(ResourceUtil.getFxmlResource(resource));
        return loader.load();
    }

    private FXMLLoader getLoader() {
        if (loader == null) {
            FXMLLoader.setDefaultClassLoader(getClass().getClassLoader());
            loader = new FXMLLoader();
            loader.setControllerFactory(getControllerFactory(stage));
            loader.setClassLoader(getClass().getClassLoader());
        }
        return loader;
    }

    private void initStageBindings(Stage stage) {
        appConfig.alwaysOnTopProperty().addListener((observable, oldValue, newValue) -> stage.setAlwaysOnTop(newValue));
        appConfig.maximizedProperty().bind(stage.maximizedProperty());
        appConfig.prefWindowWidthProperty().bind(stage.widthProperty());
        appConfig.prefWindowHeightProperty().bind(stage.heightProperty());
        stage.minWidthProperty().bind(Bindings.createDoubleBinding(() -> appConfig.isShowSettingsPane() ? MIN_STAGE_WIDTH_SETTINGS : MIN_STAGE_WIDTH_DEFAULT, appConfig.showSettingsPaneProperty()));
    }

    private void initStageProperties(Stage stage) {
        //Initialize Stage Properties
        stage.setTitle(APP_TITLE);
        stage.setIconified(false);
        stage.setAlwaysOnTop(appConfig.isAlwaysOnTop());
        stage.setMinWidth(appConfig.getMinWindowWidth());
        stage.setMinHeight(appConfig.getMinWindowHeight());
        stage.setOnCloseRequest(confirmCloseEventHandler(stage));

        if (appConfig.isMaximized())
            stage.setMaximized(appConfig.isMaximized());
        else {
            stage.setWidth(appConfig.getPrefWindowWidth());
            stage.setHeight(appConfig.getPrefWindowHeight());
        }
    }

    private EventHandler<WindowEvent> confirmCloseEventHandler(Stage stage) {
        return windowEvent -> {
            GlcdConfigApp settings = Context.getInstance().getAppConfig();

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
            if (closeResponse.isPresent() && !ButtonType.YES.equals(closeResponse.get())) {
                windowEvent.consume();
            } else {
                controller.onClose();
            }
        };
    }

    private void launchEditableGrid(Stage primaryStage) {
        try {
            int columns = 128;
            primaryStage.setWidth(10 * columns);
            int rows = 64;
            primaryStage.setHeight(10 * rows);

            StackPane root = new StackPane();
            MouseGestures mg = new MouseGestures();
            double width = 800;
            double height = 600;
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
