package com.ibasco.glcdemu.controllers;

import com.ibasco.glcdemu.services.GlcdConfigService;
import com.ibasco.glcdemu.services.GlcdConfigProfileService;
import com.ibasco.glcdemu.GlcdController;
import com.ibasco.glcdemu.beans.GlcdConfigApp;
import com.ibasco.glcdemu.beans.GlcdConfigEmulatorProfile;
import com.ibasco.glcdemu.controls.GlcdScreen;
import com.ibasco.glcdemu.model.*;
import com.ibasco.glcdemu.utils.FileUtils;
import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class GlcdEmulatorController extends GlcdController {
    private static final Logger log = LoggerFactory.getLogger(GlcdEmulatorController.class);

    //<editor-fold desc="FXML Fields">
    @FXML
    public ScrollPane scpGlcd;

    @FXML
    public HBox hbGlcd;

    @FXML
    public HBox hbPins;

    @FXML
    public ToggleButton btnShowLog;

    @FXML
    public AnchorPane pLog;

    @FXML
    public GlcdScreen glcdScreen;

    @FXML
    public TableView<GlcdLog> tvLog;

    @FXML
    public ComboBox cbSize;

    @FXML
    public ComboBox cbCommType;

    @FXML
    public ComboBox cbController;

    @FXML
    public TextField tfListenIp;

    @FXML
    public TextField tfListenPort;

    @FXML
    public AnchorPane apGlcd;

    @FXML
    public Label lblStatus;

    @FXML
    public ToggleButton btnShowPinActivity;

    @FXML
    public BorderPane bpGlcd;

    @FXML
    public ToggleButton btnShowSettings;

    @FXML
    public TabPane tpSettings;

    @FXML
    public VBox vbRoot;

    @FXML
    public CheckMenuItem menuAlwaysOnTop;

    @FXML
    public ColorPicker cpBackground;

    @FXML
    public ColorPicker cpForeground;

    @FXML
    public RadioButton rbSizeFixed;

    @FXML
    public RadioButton rbSizeCustom;

    @FXML
    public ToggleGroup displaySize;

    @FXML
    public TextField tfCustomSizeWidth;

    @FXML
    public TextField tfCustomSizeHeight;

    @FXML
    public Button btnSaveSettings;

    @FXML
    public Slider slPixelSize;

    @FXML
    public MenuItem menuOpenFontBrowser;

    @FXML
    public MenuItem menuExit;

    @FXML
    public ToggleGroup menuThemes;

    @FXML
    public CheckMenuItem menuSettings;

    @FXML
    public CheckMenuItem menuPinActivity;

    @FXML
    public CheckMenuItem menuLogActivity;

    @FXML
    public RadioMenuItem menuThemeDark;

    @FXML
    public RadioMenuItem menuThemeDefault;

    @FXML
    public MenuItem menuLoadSettings;

    @FXML
    public MenuItem menuSaveSettings;

    @FXML
    public MenuItem menuSaveScreenAs;

    @FXML
    public MenuItem menuSaveScreen;

    @FXML
    public Button btnApplySettings;

    @FXML
    public CheckBox cbConfirmExit;

    @FXML
    public CheckBox cbAutoSaveSettings;

    @FXML
    public Label lblStatusHeader;

    @FXML
    public Label lblDisplaySizeHeader;

    @FXML
    public Label lblDisplaySize;

    @FXML
    public MenuItem menuCheckUpdates;

    @FXML
    public ToolBar tbMain;

    @FXML
    public HBox hbStatusBar;

    @FXML
    public MenuBar mbMain;

    @FXML
    public CheckBox cbFitWindowToScreen;

    @FXML
    public Button btnOpenScreenshotPath;

    @FXML
    public TextField tfScreenshotPath;

    @FXML
    public ListView<GlcdConfigEmulatorProfile> lvProfiles;

    @FXML
    public Button btnSaveProfile;

    @FXML
    public TextField tfProfileName;

    @FXML
    public Button btnDeleteProfile;

    @FXML
    public Button btnLoadProfile;

    @FXML
    public TextField tfProfileFilter;

    @FXML
    public Button btnNewProfile;

    @FXML
    public TextField tfProfileDirPath;

    @FXML
    public Button btnMakeDefault;

    @FXML
    public Button btnOpenProfileDirPath;
    //</editor-fold>

    private DateTimeFormatter imageFileNameFormatter = DateTimeFormatter.ofPattern("YYYYMMddkkmmss'_GlcdCapture'");

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private Stage stage;

    private GlcdConfigApp appConfig;

    private GlcdConfigEmulatorProfile activeProfile;

    private ChangeListener<String> clUpdateListenPortSettings = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (!newValue.matches("\\d*")) {
                tfListenPort.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (NumberUtils.isDigits(newValue))
                activeProfile.setListenPort(Integer.valueOf(newValue));
        }
    };

    private static final double MIN_STAGE_WIDTH_SETTINGS = 848.0;

    private static final double MIN_STAGE_WIDTH_DEFAULT = 100.0;

    private ChangeListener<String> createNonNumericInputFilter(Consumer<Integer> c) {
        return (observable, oldValue, newValue) -> {
            TextField textField = (TextField) ((Property) observable).getBean();
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            } else {
                try {
                    int value = Integer.parseInt(newValue);
                    c.accept(value);
                } catch (NumberFormatException e) {
                    log.warn("Value is not a valid integer = {}", newValue);
                }
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scpGlcd.widthProperty().addListener((observable, oldValue, newValue) -> hbGlcd.setPrefWidth((Double) newValue - 3));
        scpGlcd.heightProperty().addListener((observable, oldValue, newValue) -> hbGlcd.setPrefHeight((Double) newValue - 3));

        //spGlcd.getItems().remove(pLog);
        glcdScreen.addEventHandler(MouseEvent.MOUSE_MOVED, this::onGlcdMouseMove);

        hbPins.getChildren().add(new Circle(10, Color.RED));
        hbPins.getChildren().add(new Circle(10, Color.DIMGRAY));
        hbPins.getChildren().add(new Circle(10, Color.YELLOW));
        hbPins.getChildren().add(new Circle(10, Color.DIMGRAY));
        hbPins.getChildren().add(new Circle(10, Color.GREEN));

        initializeLogView();

        profileManager = GlcdConfigService.getProfileManager();

        lvProfiles.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                GlcdConfigEmulatorProfile profile = lvProfiles.getSelectionModel().getSelectedItem();
                log.info("Selected: {}", profile.getName());
                //lvProfiles.edit(lvProfiles.getSelectionModel().getSelectedIndex());
            }
        });

        lvProfiles.setCellFactory(param -> new ListCell<GlcdConfigEmulatorProfile>() {
            @Override
            protected void updateItem(GlcdConfigEmulatorProfile item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || StringUtils.isBlank(item.getName())) {
                    setText(null);
                } else {
                    String name = StringUtils.capitalize(item.getName());
                    setText(profileManager.isDefault(item) ? name + " (Default)" : name);
                }
            }
        });

        buildProfileContextMenu();

        //Get App Config
        appConfig = GlcdConfigService.getAppConfig();
    }

    private void buildProfileContextMenu() {
        ContextMenu profileContextMenu = new ContextMenu();

        MenuItem miSetDefault = new MenuItem("Set as Default");
        miSetDefault.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                log.info("Set Default");
            }
        });
        MenuItem miDuplicate = new MenuItem("Duplicate");
        miDuplicate.setOnAction(event -> {
            GlcdConfigEmulatorProfile selectedProfile = lvProfiles.getSelectionModel().getSelectedItem();
            TextInputDialog textInputDialog = new TextInputDialog(profileManager.createDefaultDuplicateName(selectedProfile));
            textInputDialog.setTitle("Duplicate Profile");
            textInputDialog.setHeaderText("New Profile");
            textInputDialog.setContentText("Enter new profile name");
            Optional<String> result = textInputDialog.showAndWait();
            if (result.isPresent()) {
                if (profileManager.create(result.get(), selectedProfile) != null) {
                    log.info("Duplicate success");
                }
            }
        });
        MenuItem miRename = new MenuItem("Rename");
        MenuItem miDelete = new MenuItem("Delete");

        profileContextMenu.getItems().addAll(miSetDefault, miDuplicate, miRename, miDelete);

        lvProfiles.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                profileContextMenu.show(lvProfiles, event.getScreenX(), event.getScreenY());
            }
        });
    }

    @Override
    public void onInit() {
        this.stage = (Stage) vbRoot.getScene().getWindow();
        //Loads the default active profile
        loadSettings();

        //Update activeProfile on these change events
        slPixelSize.valueProperty().addListener((observable, oldValue, newValue) -> activeProfile.setPixelSize((Double) newValue));
        tfListenIp.textProperty().addListener((observable, oldValue, newValue) -> activeProfile.setListenIp(newValue));
        tfListenPort.textProperty().addListener(clUpdateListenPortSettings);

        tfCustomSizeWidth.textProperty().addListener(createNonNumericInputFilter(activeProfile::setDisplaySizeWidth));
        tfCustomSizeHeight.textProperty().addListener(createNonNumericInputFilter(activeProfile::setDisplaySizeHeight));

        tfScreenshotPath.textProperty().addListener((observable, oldValue, newValue) -> activeProfile.setScreenshotDirPath(newValue));

        stage.widthProperty().addListener((observable, oldValue, newValue) -> activeProfile.setPrefWindowWidth((Double) newValue));
        stage.heightProperty().addListener((observable, oldValue, newValue) -> activeProfile.setPrefWindowHeight((Double) newValue));
        tvLog.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tfProfileFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<GlcdConfigEmulatorProfile> filteredProfiles = lvProfiles.getItems().filtered(GlcdConfigProfileService.Predicates.containsName(newValue));
            log.info("Found: {}", filteredProfiles.size());
        });

        applySettings(activeProfile);

        refreshProfiles();
    }

    @Override
    public void onClose() {
        if (activeProfile.isRememberSettingsOnExit()) {
            applySettings(activeProfile);
            saveSettings();
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeLogView() {
        ObservableList<GlcdLog> entries = FXCollections.observableArrayList();
        entries.addListener((ListChangeListener<GlcdLog>) c -> {
            Platform.runLater(() -> tvLog.scrollTo(tvLog.getItems().size() - 1));
        });

        TableColumn<GlcdLog, LocalDateTime> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        timestampCol.setCellFactory(tc -> new TableCell<GlcdLog, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        TableColumn<GlcdLog, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<GlcdLog, String> dataCol = new TableColumn<>("Data");
        dataCol.setCellValueFactory(new PropertyValueFactory<>("data"));

        tvLog.getColumns().setAll(timestampCol, typeCol, dataCol);
        tvLog.setItems(entries);
    }

    private void onGlcdMouseMove(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED) {
            String pos = "X=" + mouseEvent.getX() + ", Y=" + mouseEvent.getY();
            tvLog.getItems().add(new GlcdLog(LocalDateTime.now(), "DATA", pos));
        }
    }

    @FXML
    public void onShowPinActivityAction(ActionEvent actionEvent) {
        ToggleButton btn = (ToggleButton) actionEvent.getSource();
        showPinActivities(btn.isSelected() && bpGlcd.getTop() == null);
    }

    @FXML
    public void onShowSettingsAction(ActionEvent actionEvent) {
        ToggleButton btn = (ToggleButton) actionEvent.getSource();
        showSettings(btn.isSelected() && bpGlcd.getBottom() == null);
    }

    @FXML
    public void onSaveSettingsAction(ActionEvent actionEvent) {
        saveSettings();
    }

    @FXML
    public void onApplySettingsAction(ActionEvent actionEvent) {
        applySettings(activeProfile);
        log.info("Settings applied");
    }

    @FXML
    public void onLcdBackgroundAction(ActionEvent actionEvent) {
        ColorPicker cp = (ColorPicker) actionEvent.getSource();
        activeProfile.setLcdBackground(cp.getValue());
        updateGlcdScreen();
    }

    @FXML
    public void onLcdForegroundAction(ActionEvent actionEvent) {
        ColorPicker cp = (ColorPicker) actionEvent.getSource();
        activeProfile.setLcdForeground(cp.getValue());
        updateGlcdScreen();
    }

    @FXML
    public void onMenuSaveScreenAsAction(ActionEvent actionEvent) {
        saveScreenCaptureAs();
    }

    @FXML
    public void onMenuSaveScreenAction(ActionEvent actionEvent) {
        saveScreenCapture();
    }

    @FXML
    public void onMenuExitAction(ActionEvent actionEvent) {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @FXML
    public void onMenuAlwaysOnTopAction(ActionEvent actionEvent) {
        CheckMenuItem menuItem = (CheckMenuItem) actionEvent.getSource();
        setAlwaysOnTop(menuItem.isSelected());
    }

    public void onMenuOpenFontBrowserAction(ActionEvent actionEvent) {
    }

    public void onMenuThemeSelection(ActionEvent actionEvent) {
        RadioMenuItem radioMenuItem = (RadioMenuItem) actionEvent.getSource();
        setThemeById(radioMenuItem.getId());
    }

    public void onMenuTogglePaneAction(ActionEvent actionEvent) {
        showPane((CheckMenuItem) actionEvent.getSource());
    }

    public void onLoadSettingsAction(ActionEvent actionEvent) {
        File settingsFile = openSettingsFileFromDialog();
        if (settingsFile != null) {
            log.info("Loaded activeProfile: {}", settingsFile);
        }
    }

    public void onCheckConfirmExitAction(ActionEvent actionEvent) {
        setConfirmOnExit(cbConfirmExit.isSelected());
    }

    public void onCheckAutoSaveAction(ActionEvent actionEvent) {
        setAutoSaveOnExit(cbAutoSaveSettings.isSelected());
    }

    public void onCheckFitWindowToScreenAction(ActionEvent actionEvent) {
        activeProfile.setAutoFitWindowToScreen(cbFitWindowToScreen.isSelected());
    }

    public void onFitScreenToWindowAction(ActionEvent actionEvent) {
        fitWindowToScreen();
    }

    public void onChooseScreenshotPathAction(ActionEvent actionEvent) {
        File file = openDirFromDialog("Select screenshot directory", System.getProperty("user.dir"));
        if (file != null)
            setScreenshotDirPath(file.getAbsolutePath());
    }

    public void onResetDefaultSettingsAction(ActionEvent actionEvent) {
        resetDefaultSettings();
    }

    public void onSizeScroll(ScrollEvent scrollEvent) {
        TextField textField = (TextField) scrollEvent.getSource();
        String value = textField.getText();
        if (!StringUtils.isNumeric(value))
            return;
        int newValue = Integer.parseInt(value);
        if (scrollEvent.getDeltaY() >= 0) {
            newValue += 8;
        } else {
            newValue -= 8;
        }
        textField.setText(String.valueOf(newValue));
    }

    private String createProfileNameFromSettings() {
        return "Glcd_" +
                glcdScreen.getDisplayWidth() +
                "x" +
                glcdScreen.getDisplayHeight() +
                "_" +
                (int) activeProfile.getPixelSize() +
                "PX";
    }

    public void onProfileFilterInput(ActionEvent actionEvent) {
        log.info("YEYE");
    }

    public void onProfileSaveAction(ActionEvent actionEvent) {
        GlcdConfigEmulatorProfile profile = lvProfiles.getItems().get(0);
        profile.setName("testtest");
    }

    public void onProfileDeleteAction(ActionEvent actionEvent) {
    }

    public void onProfileLoadAction(ActionEvent actionEvent) {
    }

    public void onProfileSetDefaultAction(ActionEvent actionEvent) {

    }

    public void onProfileOpenDirPath(ActionEvent actionEvent) {
    }

    public void onProfileNewAction(ActionEvent actionEvent) {
        TextInputDialog textInputDialog = new TextInputDialog(createProfileNameFromSettings());
        textInputDialog.setTitle("New Profile");
        textInputDialog.setHeaderText("New Profile");
        textInputDialog.setContentText("Enter new profile name");
        Optional<String> result = textInputDialog.showAndWait();
        createNewProfile(result.orElse(null));
    }

    private void createNewProfile(String profileName) {
        if (StringUtils.isBlank(profileName))
            return;
        log.info("New Profile created: {}", profileName);
        GlcdConfigEmulatorProfile newProfile = profileManager.create(profileName);
        //GlcdConfigManager.save();
        log.info("New Profile: {}", newProfile);
    }

    //<editor-fold desc="Helper Methods">
    private void resetDefaultSettings() {
        activeProfile = new GlcdConfigEmulatorProfileDefault();
        applySettings(activeProfile);
        fitWindowToScreen();
    }

    private void setScreenshotDirPath(String path) {
        FileUtils.ensureDirectoryExistence(path);
        tfScreenshotPath.setText(path);
        activeProfile.setScreenshotDirPath(path);
    }

    private void fitWindowToScreen() {
        if (activeProfile.isMaximized())
            return;
        Platform.runLater(() -> {
            double padding = 60;
            double height = glcdScreen.getHeight() + hbStatusBar.getHeight() + tbMain.getHeight() + mbMain.getHeight() + (tpSettings.isVisible() ? tpSettings.getHeight() : 0) + padding;
            //double width = glcdScreen.getWidth() + (spGlcd.getItems().contains(pLog) ? pLog.getWidth() : 0) + padding;
            double width = glcdScreen.getWidth() + padding;

            if (bpGlcd.getTop() != null && bpGlcd.getTop().equals(hbPins)) {
                height += hbPins.getHeight();
            }

            stage.setWidth(width);
            stage.setHeight(height);
        });
    }

    private void setConfirmOnExit(boolean value) {
        cbConfirmExit.setSelected(value);
        activeProfile.setConfirmOnExit(value);
    }

    private void setAutoSaveOnExit(boolean value) {
        cbAutoSaveSettings.setSelected(value);
        activeProfile.setRememberSettingsOnExit(value);
    }

    private void setAlwaysOnTop(boolean value) {
        stage.setAlwaysOnTop(value);
        menuAlwaysOnTop.setSelected(value);
        activeProfile.setAlwaysOntop(value);
    }

    private void showSettings(boolean value) {
        if (value) {
            bpGlcd.setBottom(tpSettings);
            tpSettings.setVisible(true);
            stage.setMinWidth(MIN_STAGE_WIDTH_SETTINGS);
        } else {
            tpSettings.setVisible(false);
            bpGlcd.setBottom(null);
            stage.setMinWidth(MIN_STAGE_WIDTH_DEFAULT);
        }
        activeProfile.setShowSettingsPane(value);
        btnShowSettings.setSelected(value);
        menuSettings.setSelected(value);

        if (activeProfile.isAutoFitWindowToScreen()) {
            fitWindowToScreen();
        }
    }

    private void showPinActivities(boolean value) {
        if (value)
            bpGlcd.setTop(hbPins);
        else
            bpGlcd.setTop(null);
        btnShowPinActivity.setSelected(value);
        menuPinActivity.setSelected(value);
        activeProfile.setShowPinActivityPane(value);

        if (activeProfile.isAutoFitWindowToScreen()) {
            fitWindowToScreen();
        }
    }

    private void updateGlcdScreen() {
        glcdScreen.setPixelSize(activeProfile.getPixelSize());
        glcdScreen.setDisplayWidth(activeProfile.getDisplaySizeWidth());
        glcdScreen.setDisplayHeight(activeProfile.getDisplaySizeHeight());
        glcdScreen.setBackgroundColor(activeProfile.getLcdBackground());
        glcdScreen.setForegroundColor(activeProfile.getLcdForeground());
        glcdScreen.refresh();
    }

    private void saveScreenCapture() {
        FileUtils.ensureDirectoryExistence(activeProfile.getScreenshotDirPath());
        File file = new File(activeProfile.getScreenshotDirPath() + File.separator + imageFileNameFormatter.format(ZonedDateTime.now()) + ".png");
        if (!file.exists()) {
            saveScreenCapture(file);
        } else {
            log.warn("Could not save screen capture file '{}'. File already exists", file.getAbsolutePath());
        }
    }

    private void saveScreenCaptureAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        fileChooser.setInitialFileName(imageFileNameFormatter.format(ZonedDateTime.now()));
        if (!StringUtils.isBlank(activeProfile.getLastSavedImagePath())) {
            fileChooser.setInitialDirectory(new File(activeProfile.getLastSavedImagePath()));
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "png"));
        File file = fileChooser.showSaveDialog(stage);
        saveScreenCapture(file);
        if (file != null)
            activeProfile.setLastSavedImagePath(FilenameUtils.getFullPath(file.getAbsolutePath()));
    }

    private void saveScreenCapture(File imageFile) {
        if (imageFile != null) {
            //make sure we include the extension
            if (!imageFile.getName().endsWith(".png")) {
                imageFile = new File(imageFile.getAbsolutePath() + ".png");
            }
            int pixelSize = (int) glcdScreen.getPixelSize();
            WritableImage wim = new WritableImage(glcdScreen.getDisplayWidth() * pixelSize, glcdScreen.getDisplayHeight() * pixelSize);
            glcdScreen.snapshot(null, wim);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", imageFile);
                log.info("Saved file to : {}", FilenameUtils.getFullPath(imageFile.getAbsolutePath()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void setThemeById(String id) {
        switch (id) {
            case "menuThemeDefault":
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(ResourceUtil.getStylesheet("app.css").toExternalForm());
                menuThemeDefault.setSelected(true);
                break;
            case "menuThemeDark":
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(ResourceUtil.getStylesheet("app-dark.css").toExternalForm());
                menuThemeDark.setSelected(true);
                break;
            default:
                throw new RuntimeException("Invalid theme ID: " + id);
        }
        activeProfile.setThemeId(id);
    }

    private void showPane(CheckMenuItem item) {
        switch (item.getId()) {
            case "menuPinActivity":
                showPinActivities(item.isSelected());
                break;
            case "menuSettings":
                showSettings(item.isSelected());
                break;
        }
    }

    private void refreshProfiles() {
        lvProfiles.setItems(profileManager.getProfiles());
    }

    private void saveSettings() {
        try {
            //Save App Config
            GlcdConfigService.save(appConfig);

            //Save Profiles
            profileManager.getProfiles();
            profileManager.save();
            log.info("Settings Saved");
        } catch (IOException e) {
            log.error("Could not save activeProfile", e);
        }
    }

    private void loadSettings() {
        activeProfile = GlcdConfigService.getProfileManager().getActiveProfile();
        appConfig = GlcdConfigService.getAppConfig();
    }

    private void setLcdBackgroundColor(Color value) {
        cpBackground.setValue(value);
        activeProfile.setLcdBackground(value);
    }

    private void setLcdForegroundColor(Color value) {
        cpForeground.setValue(value);
        activeProfile.setLcdForeground(value);
    }

    private void setListenIP(String ip) {
        tfListenIp.setText(ip);
        activeProfile.setListenIp(ip);
    }

    private void setListenPort(int port) {
        tfListenPort.setText(String.valueOf(port));
        activeProfile.setListenPort(port);
    }

    private void setDisplayPixelSize(double pixelSize) {
        slPixelSize.setValue(pixelSize);
        activeProfile.setPixelSize(pixelSize);
    }

    private void setDisplayCustomSize(int width, int height) {
        if (-1 != width) {
            tfCustomSizeWidth.setText(String.valueOf(width));
            activeProfile.setDisplaySizeWidth(width);
        }
        if (-1 != height) {
            tfCustomSizeHeight.setText(String.valueOf(height));
            activeProfile.setDisplaySizeHeight(height);
        }
    }

    /**
     * Update GUI nodes based on the activeProfile
     */
    private void applySettings(GlcdConfigEmulatorProfile settings) {
        showSettings(settings.isShowSettingsPane());
        showPinActivities(settings.isShowPinActivityPane());
        setAlwaysOnTop(settings.isAlwaysOntop());

        //LCD Background and foreground
        setLcdBackgroundColor(settings.getLcdBackground());
        setLcdForegroundColor(settings.getLcdForeground());

        //Listen IP and Port
        setListenIP(settings.getListenIp());
        setListenPort(settings.getListenPort());

        //Apply display properties
        setDisplayCustomSize(settings.getDisplaySizeWidth(), settings.getDisplaySizeHeight());
        setDisplayPixelSize(settings.getPixelSize());

        setScreenshotDirPath(settings.getScreenshotDirPath());

        setThemeById(settings.getThemeId());

        updateGlcdScreen();

        setConfirmOnExit(settings.isConfirmOnExit());
        setAutoSaveOnExit(settings.isRememberSettingsOnExit());

        cbFitWindowToScreen.setSelected(settings.isAutoFitWindowToScreen());

        //TODO: Update
        profileManager.update(activeProfile);
    }

    private void updateGUIState() {

    }

    private File openSettingsFileFromDialog() {
        File file = openFileFromDialog("Open Settings", activeProfile.getLastOpenFilePath(), new FileChooser.ExtensionFilter("Emulator Settings", "*.json"));
        if (file != null)
            activeProfile.setLastOpenFilePath(FilenameUtils.getFullPath(file.getAbsolutePath()));
        return file;
    }

    private File openFileFromDialog(String title, String initDirectory, FileChooser.ExtensionFilter extFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (!StringUtils.isBlank(initDirectory)) {
            fileChooser.setInitialDirectory(new File(initDirectory));
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.getExtensionFilters().add(extFilters);
        return fileChooser.showOpenDialog(stage);
    }

    private File openDirFromDialog(String title, String initDirectory) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(title);
        if (!StringUtils.isBlank(initDirectory)) {
            dirChooser.setInitialDirectory(new File(initDirectory));
        } else {
            dirChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        return dirChooser.showDialog(stage);
    }

    //</editor-fold>
}
