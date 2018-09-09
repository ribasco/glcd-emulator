package com.ibasco.glcdemu.controllers;

import com.ibasco.glcdemu.*;
import com.ibasco.glcdemu.constants.Common;
import com.ibasco.glcdemu.controls.GlcdScreen;
import com.ibasco.glcdemu.emulator.GlcdEmulator;
import com.ibasco.glcdemu.emulator.st7920.ST7920Emulator;
import com.ibasco.glcdemu.enums.PixelShape;
import com.ibasco.glcdemu.model.GlcdConfigApp;
import com.ibasco.glcdemu.model.GlcdEmulatorProfile;
import com.ibasco.glcdemu.model.GlcdLog;
import com.ibasco.glcdemu.services.EmulatorService;
import com.ibasco.glcdemu.utils.BindGroup;
import com.ibasco.glcdemu.utils.DialogUtil;
import com.ibasco.glcdemu.utils.FileUtils;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.jfoenix.controls.*;
import com.sun.javafx.event.EventUtil;
import com.sun.javafx.stage.StageHelper;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"Duplicates", "SameParameterValue"})
public class GlcdEmulatorController extends GlcdController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GlcdEmulatorController.class);

    //<editor-fold desc="FXML Fields">
    @FXML
    public VBox vbRoot;

    @FXML
    private JFXTabPane tpSettings;

    @FXML
    public MenuBar mbMain;

    @FXML
    public MenuItem menuSaveSettings;

    @FXML
    public Pane sizePane;

    @FXML
    public AnchorPane apProfiles;

    @FXML
    private MenuItem menuEmulatorControl;

    @FXML
    private MenuItem menuSaveScreen;

    @FXML
    private MenuItem menuSaveScreenAs;

    @FXML
    private MenuItem menuExit;

    @FXML
    private CheckMenuItem menuAlwaysOnTop;

    @FXML
    private CheckMenuItem menuShowToolbar;

    @FXML
    private MenuItem menuFitScreenToWindow;

    @FXML
    private MenuItem menuOpenFontBrowser;

    @FXML
    private CheckMenuItem menuSettings;

    @FXML
    private CheckMenuItem menuPinActivity;

    @FXML
    private RadioMenuItem menuThemeDefault;

    @FXML
    private ToggleGroup menuThemes;

    @FXML
    private RadioMenuItem menuThemeDark;

    @FXML
    private MenuItem menuCheckUpdates;

    @FXML
    private ToolBar tbMain;

    @FXML
    private JFXButton btnFitScreenToWindow;

    @FXML
    private JFXToggleButton btnShowPinActivity;

    @FXML
    private JFXToggleButton btnShowSettings;

    @FXML
    private AnchorPane apGlcd;

    @FXML
    private BorderPane bpGlcd;

    @FXML
    private HBox hbPins;

    @FXML
    private ScrollPane scpGlcd;

    @FXML
    private HBox hbGlcd;

    @FXML
    private GlcdScreen glcdScreen;

    @FXML
    private JFXColorPicker cpBacklight;

    @FXML
    private JFXColorPicker cpInactivePixel;

    @FXML
    private JFXColorPicker cpActivePixel;

    @FXML
    private JFXSlider slPixelSize;

    @FXML
    private JFXButton btnReset;

    @FXML
    private JFXTextField tfProfileFilter;

    @FXML
    private JFXTextField tfProfileDirPath;

    @FXML
    private JFXButton btnOpenProfileDirPath;

    @FXML
    private JFXCheckBox cbConfirmExit;

    @FXML
    private JFXCheckBox cbAutoSaveSettings;

    @FXML
    private JFXTextField tfScreenshotPath;

    @FXML
    private JFXButton btnOpenScreenshotPath;

    @FXML
    private JFXCheckBox cbFitWindowToScreen;

    @FXML
    private Spinner<Integer> spnDisplayWidth;

    @FXML
    private Spinner<Integer> spnDisplayHeight;

    @FXML
    private JFXTextField tfListenIp;

    @FXML
    private Spinner<Integer> spnListenPort;

    @FXML
    private TableView tvLog;

    @FXML
    private HBox hbStatusBar;

    @FXML
    private Label lblStatusHeader;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblDisplaySizeHeader;

    @FXML
    private Label lblDisplaySize;

    @FXML
    private TableView<GlcdEmulatorProfile> tvProfiles;

    @FXML
    private JFXSlider slContrast;

    @FXML
    private JFXSlider slMargin;

    @FXML
    private JFXSlider slPixelSpacing;

    @FXML
    private JFXComboBox<PixelShape> cbPixelShape;

    @FXML
    private JFXButton btnDonate;

    @FXML
    private JFXButton btnToolBarSave;

    @FXML
    private JFXToggleButton tbListen;

    @FXML
    private JFXCheckBox cbRunEmulatorStartup;
    //</editor-fold>

    private ObservableList<GlcdLog> logEntries = FXCollections.observableArrayList();

    private final DateTimeFormatter imageFileNameFormatter = DateTimeFormatter.ofPattern("YYYYMMddkkmmss'_GlcdCapture'");

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private final AtomicReference<GlcdEmulatorProfile> selectedProfileContext = new AtomicReference<>(null);

    private final AtomicBoolean modifierCtrlPressed = new AtomicBoolean(false);

    private GlcdConfigApp appConfig = getContext().getAppConfig();

    private BindGroup appBindGroup = new BindGroup();

    private BindGroup profileBindGroup = new BindGroup();

    private DecimalFormat decimalFormatter = new DecimalFormat("##.00");

    private FadeTransition screenshotTransition;

    private ObjectProperty<PixelBuffer> displayBuffer = new SimpleObjectProperty<>();

    private EmulatorService emulatorService;

    @FunctionalInterface
    private interface CommandNoArg {
        void execute();
    }

    private ExecutorService taskService = Executors.newFixedThreadPool(5, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private EventHandler<KeyEvent> profileTableKeyEventHandler = event -> {
        if (event.getCode() == KeyCode.ENTER) {
            profileLoadSelected();
        } else if (event.getCode() == KeyCode.DELETE) {
            profileDeleteAction(null);
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        screenshotTransition = new FadeTransition(Duration.millis(500), glcdScreen);
        screenshotTransition.setFromValue(0.0);
        screenshotTransition.setToValue(1.0);
        screenshotTransition.setAutoReverse(false);
        screenshotTransition.setCycleCount(1);
        screenshotTransition.setInterpolator(Interpolator.EASE_BOTH);

        //Listen invalidation changes in active profile property
        getContext().getProfileManager().activeProfileProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Profile  from '{}' to '{}'", oldValue, newValue);
            activateProfile(newValue);
        });

        appConfig.defaultProfileIdProperty().addListener((observable, oldValue, newValue) -> log.info("Default profile changed from {} to {}", oldValue, newValue));
        attachAutoFitWindowBindings(glcdScreen.widthProperty());
        attachAutoFitWindowBindings(glcdScreen.heightProperty());

        setupNodeProperties();
        applyDefaultProfile();
        updateProfileBindings(getContext().getProfileManager().getActiveProfile());
        updateAppBindings(appConfig);
        applyTheme(appConfig.getThemeId());

        Context.getPrimaryStage().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            log.debug("Close Event Filter from: Controller");
            profileCheckModified(!appConfig.isConfirmOnExit(), !appConfig.isRememberSettingsOnExit());
            if (appConfig.isRememberSettingsOnExit()) {
                saveAppSettings();
            }

            if (emulatorService.isRunning()) {
                if (emulatorService.isClientConnected()) {
                    if (!DialogUtil.promptConfirmation("A client is currently connected", "Emulator service is still running and a client is currently connected, are you sure you want to quit?")) {
                        event.consume();
                        return;
                    }
                }
                stopEmulatorService();
            }
        });
    }

    private void activateProfile(GlcdEmulatorProfile newValue) {
        glcdScreen.stop();
        try {
            updateProfileBindings(newValue);
            //Refresh the display buffer
            refreshDisplayBuffer();
        } finally {
            glcdScreen.start();
        }

    }

    private void attachAutoFitWindowBindings(ObservableValue<Number> numberProperty) {
        numberProperty.addListener((observable, oldValue, newValue) -> {
            if (!appConfig.isMaximized() && appConfig.isAutoFitWindowToScreen()) {
                fitWindowToScreen();
            }
        });
    }

    private void refreshDisplayBuffer() {
        GlcdEmulatorProfile activeProfile = getContext().getProfileManager().getActiveProfile();
        displayBuffer.set(new PixelBuffer(activeProfile.getDisplaySizeWidth(), activeProfile.getDisplaySizeHeight()));

        log.debug("Display buffer refreshed (Width: {}, Height: {})", activeProfile.getDisplaySizeWidth(), activeProfile.getDisplaySizeHeight());
    }

    private void buildProfileContextMenu() {
        ContextMenu profileContextMenu = new ContextMenu();
        MenuItem miProfileNew = new MenuItem("New Profile");
        miProfileNew.setOnAction(this::profileNewAction);

        MenuItem miSetDefault = new MenuItem("Set as Default");
        miSetDefault.setOnAction(event -> updateDefaultProfile(tvProfiles.getSelectionModel().getSelectedItem()));

        MenuItem miDuplicate = new MenuItem("Duplicate");
        miDuplicate.setOnAction(this::profileDuplicateAction);

        MenuItem miEditProfile = new MenuItem("Edit");
        miEditProfile.setOnAction(this::profileEditAction);

        MenuItem miDelete = new MenuItem("Delete");
        miDelete.setOnAction(this::profileDeleteAction);

        profileContextMenu.getItems().addAll(miProfileNew, miSetDefault, miDuplicate, miEditProfile, miDelete);

        tvProfiles.setOnContextMenuRequested(event -> {
            selectedProfileContext.set(tvProfiles.getSelectionModel().getSelectedItem());
            profileContextMenu.show(tvProfiles, event.getScreenX(), event.getScreenY());
        });

        //Hide context menu if selection has changed
        tvProfiles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && profileContextMenu.isShowing() && !newValue.equals(selectedProfileContext.getAndSet(null))) {
                profileContextMenu.hide();
            }
        });
    }

    private void setupProfileTable() {
        buildProfileContextMenu();
        tvProfiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tvProfiles.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tvProfiles.setOnKeyReleased(profileTableKeyEventHandler);
        tvProfiles.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2)
                profileLoadSelected();
        });

        TableColumn<GlcdEmulatorProfile, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(param -> Bindings.createBooleanBinding(() -> {
            if (param.getValue() == null)
                return false;
            return getContext().getProfileManager().getActiveProfile().getId() == param.getValue().getId();
        }, getContext().getProfileManager().activeProfileProperty()));

        activeCol.setCellFactory(tc -> new TableCell<GlcdEmulatorProfile, Boolean>() {
            @Override
            protected void updateItem(Boolean activeState, boolean empty) {
                super.updateItem(activeState, empty);
                if (empty)
                    setText(null);
                else
                    setText(activeState != null && activeState ? "Yes" : "No");
            }
        });

        TableColumn<GlcdEmulatorProfile, Boolean> defaultCol = new TableColumn<>("Default");
        defaultCol.setCellValueFactory(param -> Bindings.createBooleanBinding(() -> {
            if (param.getValue() == null)
                return false;
            return appConfig.getDefaultProfileId() == param.getValue().getId();
        }, appConfig.defaultProfileIdProperty()));

        defaultCol.setCellFactory(tc -> new TableCell<GlcdEmulatorProfile, Boolean>() {
            @Override
            protected void updateItem(Boolean defaultState, boolean empty) {
                super.updateItem(defaultState, empty);
                if (empty)
                    setText(null);
                else
                    setText(defaultState != null && defaultState ? "Yes" : "No");
            }
        });


        TableColumn<GlcdEmulatorProfile, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<GlcdEmulatorProfile, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<GlcdEmulatorProfile, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<GlcdEmulatorProfile, String> fileCol = new TableColumn<>("Config Path");
        fileCol.setCellValueFactory(new PropertyValueFactory<>("file"));

        TableColumn<GlcdEmulatorProfile, String> displayWidthCol = new TableColumn<>("Width");
        displayWidthCol.setCellValueFactory(new PropertyValueFactory<>("displaySizeWidth"));

        TableColumn<GlcdEmulatorProfile, String> displayHeightCol = new TableColumn<>("Height");
        displayHeightCol.setCellValueFactory(new PropertyValueFactory<>("displaySizeHeight"));

        TableColumn<GlcdEmulatorProfile, Double> pixelSizeCol = new TableColumn<>("Pixel Size");
        pixelSizeCol.setCellValueFactory(new PropertyValueFactory<>("lcdPixelSize"));
        pixelSizeCol.setCellFactory(tc -> new TableCell<GlcdEmulatorProfile, Double>() {
            @Override
            protected void updateItem(Double size, boolean empty) {
                super.updateItem(size, empty);
                if (empty)
                    setText(null);
                else {
                    setText(decimalFormatter.format(size));
                }
            }
        });

        //noinspection unchecked
        tvProfiles.getColumns().setAll(idCol, nameCol, descCol, displayWidthCol, displayHeightCol, pixelSizeCol, activeCol, defaultCol, fileCol);
    }

    private void updateDefaultProfile(GlcdEmulatorProfile profile) {
        if (profile != null && !appConfig.isDefault(profile)) {
            String header = "Are you sure you want to set '" + profile.getName() + "' as the default profile?";
            String content = "The selected profile will be applied on the next application start";
            if (DialogUtil.promptConfirmation(header, content)) {
                appConfig.setDefaultProfile(profile);
            }
        }
    }

    /**
     * Check if we have a default profile stored in the File System.
     */
    private void applyDefaultProfile() {
        //Load default profile
        int defaultProfileId = appConfig.getDefaultProfileId();
        GlcdProfileManager profileManager = getContext().getProfileManager();
        GlcdEmulatorProfile defaultProfile = profileManager.getProfile(defaultProfileId);
        if (defaultProfile == null) {
            log.debug("No default profile found in file system for id '{}', using default", defaultProfileId);
            defaultProfile = new GlcdEmulatorProfile();
        }
        profileManager.setActiveProfile(defaultProfile);
        log.debug("Loaded default profile: {}", defaultProfile);
    }

    private void updateSliderOnScroll(ScrollEvent event) {
        Slider slider = (Slider) event.getSource();
        if (event.getDeltaY() >= 0)
            slider.increment();
        else
            slider.decrement();
    }

    private void resizeLcdOnScroll(ScrollEvent event) {
        GlcdEmulatorProfile activeProfile = getContext().getProfileManager().getActiveProfile();
        double step = modifierCtrlPressed.get() ? 1.0 : 0.1;
        if (event.getDeltaY() >= 0)
            activeProfile.incrementPixel(step);
        else
            activeProfile.decrementPixel(step);
    }

    @SuppressWarnings("unchecked")
    private void initializeLogView() {
        logEntries.addListener((ListChangeListener<GlcdLog>) c -> {
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
        tvLog.setItems(logEntries);
    }

    private void initEmulatorService() {
        if (emulatorService != null)
            return;

        emulatorService = new EmulatorService();
        GlcdEmulator emulator = new ST7920Emulator();
        emulator.bufferProperty().bind(displayBuffer);
        emulatorService.listenIpProperty().bind(appConfig.listenIpProperty());
        emulatorService.listenPortProperty().bind(appConfig.listenPortProperty());
        emulatorService.setExecutor(taskService);
        emulatorService.setEmulator(emulator);
        emulatorService.runningProperty().addListener((observable, oldValue, newValue) -> {
            tbListen.setSelected(newValue);
            sizePane.setDisable(newValue);
            apProfiles.setDisable(newValue);
            tfListenIp.setDisable(newValue);
            spnListenPort.setDisable(newValue);
            btnReset.setDisable(newValue);
        });

        StringBinding statusBinding = Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() {
                return " Emulator: " + (emulatorService.isRunning() ? "On" : "Off") + ", Client: " + (emulatorService.isClientConnected() ? "Connected" : "Disconnected") + ", IP/Port: " + emulatorService.getListenIp() + ":" + emulatorService.getListenPort();
            }
        }, emulatorService.runningProperty(), emulatorService.clientConnectedProperty());
        lblStatus.textProperty().bind(statusBinding);

        if (appConfig.isRunEmulatorAtStartup()) {
            startEmulatorService();
        }
    }

    /**
     * Starts the emulator listen service
     */
    private void startEmulatorService() {
        ObservableList<Stage> stages = StageHelper.getStages();
        for (Stage s : stages) {
            if ("U8G2 Font Browser".equals(s.getTitle()) && s.isShowing()) {
                DialogUtil.showInfo("Cannot start service", "You cannot start the service while the font browser is open, please close the browser first and try again");
                return;
            }
        }
        emulatorService.restart();
    }

    /**
     * Stops the emulator listen service
     */
    private void stopEmulatorService() {
        emulatorService.cancel();
    }

    /**
     * Save application settings
     */
    private void saveAppSettings() {
        try {
            getContext().getConfigService().save(appConfig);
        } catch (IOException e) {
            log.error("Unable to save app settings", e);
        }
    }

    @FXML
    private JFXTreeTableView tvTest;

    @FXML
    private JFXButton btnSaveSettings;

    @FXML
    private JFXButton btnShowFontBrowser;

    /**
     * Configure node properties once
     */
    private void setupNodeProperties() {
        btnSaveSettings.setOnAction(event -> saveAppSettings());
        btnShowFontBrowser.setOnAction(this::openFontBrowserAction);
        appConfig.toolbarVisibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!vbRoot.getChildren().contains(tbMain))
                    vbRoot.getChildren().add(1, tbMain);
            } else {
                vbRoot.getChildren().remove(tbMain);
            }
            if (appConfig.isAutoFitWindowToScreen())
                fitWindowToScreen();
        });

        if (!appConfig.isToolbarVisible()) {
            vbRoot.getChildren().remove(tbMain);
        }

        //initializeLogView();
        initEmulatorService();

        menuEmulatorControl.textProperty().bind(Bindings.createStringBinding(() -> {
            menuEmulatorControl.setUserData(emulatorService.isRunning());
            if (emulatorService.isRunning()) {
                return "Stop Service";
            }
            return "Start Service";
        }, emulatorService.runningProperty()));

        menuEmulatorControl.setOnAction(event -> {
            MenuItem menu = (MenuItem) event.getSource();
            boolean started = (boolean) menu.getUserData();
            if (started) {
                stopEmulatorService();
            } else {
                startEmulatorService();
            }
        });

        tbListen.textProperty().bind(Bindings.createStringBinding(() -> {
            if (emulatorService.isRunning()) {
                return "STOP";
            }
            return "START";
        }, emulatorService.runningProperty()));

        tbListen.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            ToggleButton button = (ToggleButton) event.getSource();
            if (emulatorService.isRunning() && emulatorService.isClientConnected() && button.isSelected()) {
                if (!DialogUtil.promptConfirmation("Are you sure you want to stop the service?", "A client is currently connected to the emulator service.")) {
                    event.consume();
                } else {
                    button.setSelected(false);
                    EventUtil.fireEvent(tbListen, new ActionEvent(button, button));
                }
            }
        });

        tbListen.addEventHandler(ActionEvent.ACTION, event -> {
            ToggleButton button = (ToggleButton) event.getSource();
            if (button.isSelected()) {
                if (!emulatorService.isRunning()) {
                    startEmulatorService();
                }
            } else {
                if (emulatorService.isRunning()) {
                    stopEmulatorService();
                }
            }
        });

        menuOpenFontBrowser.setOnAction(this::openFontBrowserAction);
        menuExit.setOnAction(event -> {
            Stage stage = Context.getPrimaryStage();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        menuSaveScreen.setOnAction(event -> saveScreenCapture());
        menuSaveScreenAs.setOnAction(event -> saveScreenCaptureAs());
        menuSaveSettings.setOnAction(event -> saveAppSettings());
        btnReset.setOnAction(this::resetToDefaultSettings);
        btnDonate.setOnAction(event -> Platform.runLater(() -> Bootstrap.getInstance().getHostServices().showDocument("http://www.ibasco.com")));
        btnOpenScreenshotPath.setOnAction(createOpenDirPathEventHandler("Select screenshot directory", tfScreenshotPath.textProperty()));
        btnOpenProfileDirPath.setOnAction(createOpenDirPathEventHandler("Select profile directory", tfProfileDirPath.textProperty()));
        btnFitScreenToWindow.setOnAction(createNoArgEventHandlerWrapper(this::fitWindowToScreen));
        menuFitScreenToWindow.setOnAction(createNoArgEventHandlerWrapper(this::fitWindowToScreen));
        slPixelSize.setOnScroll(this::updateSliderOnScroll);
        slPixelSpacing.setOnScroll(this::updateSliderOnScroll);
        slContrast.setOnScroll(this::updateSliderOnScroll);
        slMargin.setOnScroll(this::updateSliderOnScroll);
        glcdScreen.setOnScroll(this::resizeLcdOnScroll);
        cbPixelShape.setItems(FXCollections.observableArrayList(Arrays.asList(PixelShape.values())));
        cbPixelShape.setConverter(new StringConverter<PixelShape>() {
            @Override
            public String toString(PixelShape object) {
                return StringUtils.capitalize(object.name().toLowerCase());
            }

            @Override
            public PixelShape fromString(String string) {
                return PixelShape.valueOf(string);
            }
        });

        setupIntegerSpinner(spnListenPort, 0, 65535, 1);
        setupIntegerSpinner(spnDisplayWidth, 8, 256, 8);
        setupIntegerSpinner(spnDisplayHeight, 8, 256, 8);
        setupProfileTable();

        appConfig.autoFitWindowToScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                fitWindowToScreen();
        });

        Stage stage = Context.getPrimaryStage();

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                modifierCtrlPressed.set(true);
            }
        });

        stage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                modifierCtrlPressed.set(false);
            }
        });
    }

    private void resetToDefaultSettings(ActionEvent event) {
        if (DialogUtil.promptConfirmation("Reset active profile to default settings?", "")) {
            GlcdProfileManager profileManager = getContext().getProfileManager();

            GlcdEmulatorProfile activeProfile = profileManager.getActiveProfile();

            //Retain active profile's identity, everything else should reset to default
            GlcdEmulatorProfile newProfile = new GlcdEmulatorProfile();
            newProfile.setId(activeProfile.getId());
            newProfile.setFile(activeProfile.getFile());
            newProfile.setName(activeProfile.getName());

            profileManager.getProfiles().remove(activeProfile);
            profileManager.setActiveProfile(newProfile);
            profileManager.getProfiles().add(newProfile);

            activateProfile(newProfile);
        }
    }

    private void updateAppBindings(GlcdConfigApp appConfig) {
        if (appConfig == null)
            return;

        appBindGroup.clear();
        appBindGroup.registerBidirectional(glcdScreen.bufferProperty(), displayBuffer);
        appBindGroup.registerUnidirectional(hbGlcd.prefWidthProperty(), Bindings.subtract(scpGlcd.widthProperty(), 3));
        appBindGroup.registerUnidirectional(hbGlcd.prefHeightProperty(), Bindings.subtract(scpGlcd.heightProperty(), 3));

        appBindGroup.registerUnidirectional(bpGlcd.topProperty(), createShowNodeBinding(hbPins, appConfig.showPinActivityPaneProperty()));
        appBindGroup.registerUnidirectional(bpGlcd.bottomProperty(), createShowNodeBinding(tpSettings, appConfig.showSettingsPaneProperty()));
        appBindGroup.registerUnidirectional(tvProfiles.itemsProperty(), getContext().getProfileManager().filteredProfilesProperty());
        appBindGroup.registerUnidirectional(getContext().getProfileManager().filterProperty(), createFilterObjectBinding());

        appBindGroup.registerBidirectional(menuShowToolbar.selectedProperty(), appConfig.toolbarVisibleProperty());
        appBindGroup.registerBidirectional(menuAlwaysOnTop.selectedProperty(), appConfig.alwaysOnTopProperty());
        appBindGroup.registerBidirectional(tfProfileDirPath.textProperty(), appConfig.profileDirPathProperty());
        appBindGroup.registerBidirectional(cbRunEmulatorStartup.selectedProperty(), appConfig.runEmulatorAtStartupProperty());
        appBindGroup.registerBidirectional(cbAutoSaveSettings.selectedProperty(), appConfig.rememberSettingsOnExitProperty());
        appBindGroup.registerBidirectional(cbConfirmExit.selectedProperty(), appConfig.confirmOnExitProperty());
        appBindGroup.registerBidirectional(cbFitWindowToScreen.selectedProperty(), appConfig.autoFitWindowToScreenProperty());
        appBindGroup.registerBidirectional(tfScreenshotPath.textProperty(), appConfig.screenshotDirPathProperty());
        appBindGroup.registerBidirectional(tfListenIp.textProperty(), appConfig.listenIpProperty());
        appBindGroup.registerBidirectional(menuSettings.selectedProperty(), appConfig.showSettingsPaneProperty());
        appBindGroup.registerBidirectional(menuPinActivity.selectedProperty(), appConfig.showPinActivityPaneProperty());
        appBindGroup.registerBidirectional(btnShowSettings.selectedProperty(), appConfig.showSettingsPaneProperty());
        appBindGroup.registerBidirectional(btnShowPinActivity.selectedProperty(), appConfig.showPinActivityPaneProperty());
        appBindGroup.registerBidirectional(spnListenPort.getValueFactory().valueProperty(), appConfig.listenPortProperty());

        applyThemeBindings();
        appBindGroup.bind();
    }

    private ObjectBinding<Predicate<GlcdEmulatorProfile>> createFilterObjectBinding() {
        return Bindings.createObjectBinding(() -> {
            if (StringUtils.isEmpty(tfProfileFilter.getText()))
                return p -> true;
            return p -> StringUtils.containsIgnoreCase(p.getName(), tfProfileFilter.getText())
                    || StringUtils.containsIgnoreCase(p.getDescription(), tfProfileFilter.getText())
                    || StringUtils.containsIgnoreCase(String.valueOf(p.getDisplaySizeWidth()), tfProfileFilter.getText())
                    || StringUtils.containsIgnoreCase(String.valueOf(p.getDisplaySizeHeight()), tfProfileFilter.getText())
                    || StringUtils.containsIgnoreCase(String.valueOf(p.getLcdPixelSize()), tfProfileFilter.getText());
        }, tfProfileFilter.textProperty());
    }

    /**
     * Register profile bindings. This can be safely called multiple times in case the reference of the profile model
     * changes
     *
     * @param profile
     *         The {@link GlcdEmulatorProfile} instance to apply the bindings to
     */
    private void updateProfileBindings(GlcdEmulatorProfile profile) {
        if (profile == null)
            return;

        //Clear binds (If exists)
        profileBindGroup.clear();

        //Unidirectional Bindings
        profile.displaySizeWidthProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Resizing Pixel Buffer: Old Width = {}, New Width = {}", oldValue, newValue);
            displayBuffer.get().resize(newValue, null);
        });
        profile.displaySizeHeightProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Resizing Pixel Buffer: Old Height = {}, New Height = {}", oldValue, newValue);
            displayBuffer.get().resize(null, newValue);
        });

        StringBinding displaySizeStatus = Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                int width = profile.getDisplaySizeWidth();
                int height = profile.getDisplaySizeHeight();
                return String.valueOf(width) + " x " + String.valueOf(height);
            }
        }, profile.displaySizeWidthProperty(), profile.displaySizeHeightProperty());

        profileBindGroup.registerUnidirectional(lblDisplaySize.textProperty(), displaySizeStatus);

        //Bidirectional Bindings
        profileBindGroup.registerBidirectional(glcdScreen.activePixelColorProperty(), profile.lcdActivePixelColorProperty());
        profileBindGroup.registerBidirectional(glcdScreen.inactivePixelColorProperty(), profile.lcdInactivePixelColorProperty());
        profileBindGroup.registerBidirectional(glcdScreen.backlightColorProperty(), profile.lcdBacklightColorProperty());
        profileBindGroup.registerBidirectional(glcdScreen.contrastProperty(), profile.lcdContrastProperty());
        profileBindGroup.registerBidirectional(glcdScreen.pixelSizeProperty(), profile.lcdPixelSizeProperty());
        profileBindGroup.registerBidirectional(glcdScreen.spacingProperty(), profile.lcdSpacingProperty());
        profileBindGroup.registerBidirectional(glcdScreen.marginProperty(), profile.lcdMarginProperty());
        profileBindGroup.registerBidirectional(glcdScreen.pixelShapeProperty(), profile.lcdPixelShapeProperty());

        profileBindGroup.registerBidirectional(cpBacklight.valueProperty(), profile.lcdBacklightColorProperty());
        profileBindGroup.registerBidirectional(cpInactivePixel.valueProperty(), profile.lcdInactivePixelColorProperty());
        profileBindGroup.registerBidirectional(cpActivePixel.valueProperty(), profile.lcdActivePixelColorProperty());

        profileBindGroup.registerBidirectional(slPixelSpacing.valueProperty(), profile.lcdSpacingProperty());
        profileBindGroup.registerBidirectional(slPixelSize.valueProperty(), profile.lcdPixelSizeProperty());
        profileBindGroup.registerBidirectional(spnDisplayWidth.getValueFactory().valueProperty(), profile.displaySizeWidthProperty());
        profileBindGroup.registerBidirectional(spnDisplayHeight.getValueFactory().valueProperty(), profile.displaySizeHeightProperty());
        profileBindGroup.registerBidirectional(slContrast.valueProperty(), profile.lcdContrastProperty());
        profileBindGroup.registerBidirectional(slMargin.valueProperty(), profile.lcdMarginProperty());

        profileBindGroup.registerBidirectional(cbPixelShape.valueProperty(), profile.lcdPixelShapeProperty());

        profileBindGroup.bind();
    }

    private ObjectBinding<Node> createShowNodeBinding(Node showNode, ObservableValue<Boolean> criteriaProperty) {
        return Bindings.createObjectBinding(() -> {
            try {
                return criteriaProperty.getValue() ? showNode : null;
            } finally {
                if (appConfig.isAutoFitWindowToScreen())
                    fitWindowToScreen();
            }
        }, criteriaProperty);
    }

    //<editor-fold desc="Profile Management">
    private String createProfileNameFromSettings() {
        return "Glcd_" +
                glcdScreen.getDisplayWidth() +
                "x" +
                glcdScreen.getDisplayHeight() +
                "_" +
                Math.round(getContext().getProfileManager().getActiveProfile().getLcdPixelSize()) +
                "PX";
    }

    private void profileLoadSelected() {
        GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
        GlcdEmulatorProfile activeProfile = getContext().getProfileManager().getActiveProfile();
        if (activeProfile != null && activeProfile.equals(selectedProfile))
            return;
        if (DialogUtil.promptConfirmation("Load Profile '" + selectedProfile.getName() + "'?", selectedProfile.getDescription())) {
            getContext().getProfileManager().setActiveProfile(selectedProfile);
        }
    }

    /**
     * Check for uncommitted changes in profile list
     */
    private void profileCheckModified(boolean skipConfirmation, boolean skipSave) {
        List<GlcdEmulatorProfile> modifiedProfiles = profileGetModified();
        if (modifiedProfiles.isEmpty()) {
            return;
        }
        boolean saveProfiles = false;
        if (!skipConfirmation) {
            saveProfiles = DialogUtil.promptConfirmation("Save uncommitted changes?", "You have uncommitted profile modifications, would you like to save them?");
            if (!saveProfiles)
                return;
        }
        if (!skipSave || saveProfiles)
            profileSaveAll(modifiedProfiles);
    }

    private List<GlcdEmulatorProfile> profileGetModified() {
        return getContext().getProfileManager().findList(GlcdProfileManager.Predicates.modified());
    }

    private void profileSaveAll(List<GlcdEmulatorProfile> profiles) {
        if (profiles == null || profiles.isEmpty()) {
            return;
        }
        log.debug("Saving profiles: {}", profiles);
        for (GlcdEmulatorProfile profile : profiles) {
            getContext().getProfileManager().save(profile);
            log.debug("Saved Profile: '{}' to '{}'", profile.getName(), profile.getFile());
        }
    }

    private void profileNewAction(ActionEvent event) {
        TextInputDialog textInputDialog = new TextInputDialog(createProfileNameFromSettings());
        textInputDialog.setTitle("New Profile");
        textInputDialog.setHeaderText("New Profile");
        textInputDialog.setContentText("Enter new profile name");

        boolean valid = false;
        while (!valid) {
            Optional<String> result = textInputDialog.showAndWait();
            if (result.isPresent()) {
                String newProfileName = result.get();
                //make sure the name is not existing
                if (getContext().getProfileManager().exists(newProfileName)) {
                    textInputDialog.setHeaderText("Name already exists. Please enter a unique name");
                    continue;
                }
                valid = true;
                GlcdEmulatorProfile newProfile = getContext().getProfileManager().create(newProfileName);
                newProfile.setId(getContext().getProfileManager().newUniqueId());
                getContext().getProfileManager().getProfiles().add(newProfile);
            } else {
                break;
            }
        }
    }

    private void openFontBrowserAction(ActionEvent event) {
        if (emulatorService.isRunning()) {
            DialogUtil.showInfo("Action not allowed", "You cannot use the font browser while the emulator service is running due to stability issues. Please stop the service first and try again");
            return;
        }
        Stages.getFontBrowserStage().showAndWait();
    }

    //TODO: Fix edit bug
    private void profileEditAction(ActionEvent event) {
        GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
        GlcdEditProfileController controller = Controllers.getEditProfileController();
        Stage stage = Stages.getEditProfileStage();
        controller.setProfile(selectedProfile);
        controller.setOwner(stage);
        stage.showAndWait();
    }

    private void profileDeleteAction(ActionEvent event) {
        //GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
        BooleanProperty deleteProfileInFS = new SimpleBooleanProperty(false);
        Alert confirmDeleteAlert = DialogUtil.createAlertDialogWithCheckbox(Alert.AlertType.CONFIRMATION,
                "Delete Profile Confirmation",
                "Are you sure you want to delete the selected profile(s)?",
                "", "Also delete profile in file-system",
                deleteProfileInFS::set,
                ButtonType.YES, ButtonType.NO
        );

        Optional<ButtonType> buttonPressed = confirmDeleteAlert.showAndWait();
        if (buttonPressed.isPresent() && buttonPressed.get().equals(ButtonType.YES)) {
            for (GlcdEmulatorProfile selectedProfile : tvProfiles.getSelectionModel().getSelectedItems()) {
                if (deleteProfileInFS.get()) {
                    log.info("Deleting profile from file system = {}", selectedProfile);
                    File file = selectedProfile.getFile();
                    if (file != null && file.isFile() && file.exists()) {
                        String path = file.getPath();
                        if (file.delete()) {
                            log.info("Profile successfully deleted from the file-system = {}", path);
                        } else {
                            log.warn("Unable to delete profile '{}' from the file-system", path);
                        }
                    }
                }
                getContext().getProfileManager().delete(selectedProfile);
                log.info("Deleted Profile {}", selectedProfile);
            }
        }
    }

    private void profileDuplicateAction(ActionEvent event) {
        GlcdProfileManager profileManager = getContext().getProfileManager();
        GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
        GlcdEmulatorProfile duplicatedProfile = profileManager.create("", selectedProfile);
        TextInputDialog textInputDialog = new TextInputDialog(duplicatedProfile.getName());
        textInputDialog.setTitle("Duplicate Profile");
        textInputDialog.setHeaderText("New Duplicated Profile");
        textInputDialog.setContentText("Enter new profile name");

        boolean valid = false;
        while (!valid) {
            Optional<String> result = textInputDialog.showAndWait();
            if (result.isPresent()) {
                duplicatedProfile.setName(result.get());
                if (profileManager.exists(duplicatedProfile.getName())) {
                    textInputDialog.setHeaderText("Name already exists");
                    valid = false;
                    continue;
                }
                valid = true;
                profileManager.getProfiles().add(duplicatedProfile);
            } else {
                break;
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Theme management">
    private void applyThemeBindings() {
        menuThemes.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedThemeId = Objects.toString(newValue.getUserData(), "");
                log.info("Theme changed to '{}'", selectedThemeId);
                appConfig.setThemeId(selectedThemeId);
            }
        });
    }

    private void selectThemeMenuItem(String themeId) {
        for (Toggle toggle : menuThemes.getToggles()) {
            if (themeId.equalsIgnoreCase(Objects.toString(toggle.getUserData()))) {
                if (!toggle.isSelected())
                    menuThemes.selectToggle(toggle);
            }
        }
    }

    private void applyTheme(String themeId) {
        if (StringUtils.isBlank(themeId)) {
            log.debug("Theme ID is blank. Nothing to apply: {}", themeId);
            return;
        }
        Platform.runLater(() -> {
            Context.getInstance().getThemeManager().setActiveTheme(themeId);
            //Update selection if applicable
            selectThemeMenuItem(themeId);
        });

    }
    //</editor-fold>

    private File openFileFromDialog(String title, String initDirectory, FileChooser.ExtensionFilter extFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (!StringUtils.isBlank(initDirectory)) {
            fileChooser.setInitialDirectory(new File(initDirectory));
        } else {
            fileChooser.setInitialDirectory(new File(Common.USER_DIR));
        }
        fileChooser.getExtensionFilters().add(extFilters);
        File file = fileChooser.showOpenDialog(Context.getPrimaryStage());
        if (file != null)
            appConfig.setLastOpenFilePath(FilenameUtils.getFullPath(file.getAbsolutePath()));
        return file;
    }

    private File openDirFromDialog(String title, String initDirectory) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(title);
        if (!StringUtils.isBlank(initDirectory)) {
            dirChooser.setInitialDirectory(new File(initDirectory));
        } else {
            dirChooser.setInitialDirectory(new File(Common.USER_DIR));
        }
        return dirChooser.showDialog(Context.getPrimaryStage());
    }

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

    private StringConverter<Integer> createIntToStringConverter() {
        return new StringConverter<Integer>() {
            private Integer lastKnownInteger = 0;

            @Override
            public String toString(Integer object) {
                return String.valueOf(object);
            }

            @Override
            public Integer fromString(String string) {
                if (!StringUtils.isNumeric(string)) {
                    return lastKnownInteger;
                }
                lastKnownInteger = Integer.parseInt(string);
                return lastKnownInteger;
            }
        };
    }

    private void setupIntegerSpinner(Spinner<Integer> integerSpinner, int min, int max, int incrementStep) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max);
        valueFactory.setConverter(createIntToStringConverter());
        integerSpinner.setValueFactory(valueFactory);
        integerSpinner.setOnScroll(event -> {
            Spinner<?> spinner = (Spinner) event.getSource();
            if (event.getDeltaY() >= 0)
                spinner.increment(incrementStep);
            else
                spinner.decrement(incrementStep);
        });
    }

    private void fitWindowToScreen() {
        if (appConfig.isMaximized())
            return;
        Platform.runLater(() -> {
            double padding = 40;
            double toolbarHeight = vbRoot.getChildren().contains(tbMain) ? tbMain.getHeight() : 0;
            double settingsPaneHeight = bpGlcd.getBottom() != null ? tpSettings.getHeight() : 0;
            Stage stage = Context.getPrimaryStage();
            double height = glcdScreen.getHeight() + hbStatusBar.getHeight() + toolbarHeight + mbMain.getHeight() + (stage.getHeight() - stage.getScene().getHeight()) + settingsPaneHeight + padding;
            double width = glcdScreen.getWidth() + padding;
            if (bpGlcd.getTop() != null && bpGlcd.getTop().equals(hbPins)) {
                height += hbPins.getHeight();
            }
            stage.setWidth(width);
            stage.setHeight(height);
        });
    }

    private EventHandler<ActionEvent> createNoArgEventHandlerWrapper(CommandNoArg commandNoArg) {
        return event -> commandNoArg.execute();
    }

    private void openSettingsFileAction(ActionEvent event) {
        File settingsFile = openFileFromDialog("Open Settings", appConfig.getLastOpenFilePath(), new FileChooser.ExtensionFilter("Emulator Settings", "*.json"));
    }

    private EventHandler<ActionEvent> createOpenDirPathEventHandler(String title, Property<String> bindProperty) {
        return event -> {
            File file = openDirFromDialog(title, appConfig.getLastOpenFilePath());
            if (file != null) {
                String path = file.getAbsolutePath();
                bindProperty.setValue(path);
                appConfig.setLastOpenFilePath(path);
            }
        };
    }

    private void saveScreenCapture() {
        FileUtils.ensureDirectoryExistence(appConfig.getScreenshotDirPath());
        File file = new File(appConfig.getScreenshotDirPath() + File.separator + imageFileNameFormatter.format(ZonedDateTime.now()) + ".png");
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
        if (!StringUtils.isBlank(appConfig.getLastSavedImagePath())) {
            fileChooser.setInitialDirectory(new File(appConfig.getLastSavedImagePath()));
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "png"));
        File file = fileChooser.showSaveDialog(Context.getPrimaryStage());
        saveScreenCapture(file);
        if (file != null)
            appConfig.setLastSavedImagePath(FilenameUtils.getFullPath(file.getAbsolutePath()));
    }

    private void saveScreenCapture(File imageFile) {
        if (imageFile != null) {
            //make sure we include the extension
            if (!imageFile.getName().endsWith(".png")) {
                imageFile = new File(imageFile.getAbsolutePath() + ".png");
            }
            File finalImageFile = imageFile;
            Platform.runLater(() -> {
                try {
                    glcdScreen.setWatermarkText(getContext().getProfileManager().getActiveProfile().getName() + " - " + getContext().getProfileManager().getActiveProfile().getDescription());
                    WritableImage wim = new WritableImage((int) glcdScreen.getWidth(), (int) glcdScreen.getHeight());
                    glcdScreen.screenshot(wim);
                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", finalImageFile);
                    screenshotTransition.playFromStart();
                } catch (IOException e) {
                    log.error("Error encoutnered during screen capture", e);
                }
            });
            log.info("Saved file to : {}", FilenameUtils.getFullPath(imageFile.getAbsolutePath()));
        }
    }
}
