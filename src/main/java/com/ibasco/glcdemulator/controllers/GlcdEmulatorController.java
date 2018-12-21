/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdEmulatorController.java
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
package com.ibasco.glcdemulator.controllers;

import com.fazecast.jSerialComm.SerialPort;
import com.ibasco.glcdemulator.*;
import com.ibasco.glcdemulator.annotations.Emulator;
import com.ibasco.glcdemulator.constants.Common;
import com.ibasco.glcdemulator.constants.Views;
import com.ibasco.glcdemulator.controls.GlcdScreen;
import com.ibasco.glcdemulator.controls.StatusIndicator;
import com.ibasco.glcdemulator.emulator.GlcdEmulator;
import com.ibasco.glcdemulator.enums.*;
import com.ibasco.glcdemulator.exceptions.EmulatorControllerException;
import com.ibasco.glcdemulator.model.GlcdConfigApp;
import com.ibasco.glcdemulator.model.GlcdEmulatorProfile;
import com.ibasco.glcdemulator.net.ListenerOptions;
import com.ibasco.glcdemulator.net.serial.SerialListenerOptions;
import com.ibasco.glcdemulator.net.tcp.TcpListenerOptions;
import com.ibasco.glcdemulator.services.DisplayService;
import com.ibasco.glcdemulator.services.DrawTestService;
import com.ibasco.glcdemulator.services.ScannerService;
import com.ibasco.glcdemulator.services.SerialPortService;
import com.ibasco.glcdemulator.utils.*;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusType;
import com.jfoenix.controls.*;
import com.sun.javafx.event.EventUtil;
import com.sun.javafx.stage.StageHelper;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("Duplicates")
public class GlcdEmulatorController extends Controller {

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
    private ToggleGroup menuThemes;

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
    private JFXToggleButton tbListen;

    @FXML
    private JFXCheckBox cbRunEmulatorStartup;

    @FXML
    private JFXButton btnSaveSettings;

    @FXML
    private JFXButton btnShowFontBrowser;

    @FXML
    private JFXButton btnClearDisplay;

    @FXML
    private JFXButton btnDrawAnimTest;

    @FXML
    private JFXButton btnTcpListenTest;

    @FXML
    private JFXButton btnFreezeDisplay;

    @FXML
    private JFXButton btnTcpGetIP;

    @FXML
    private ToggleGroup connType;

    @FXML
    private JFXRadioButton rbConnTypeTcp;

    @FXML
    private JFXRadioButton rbConnTypeSerial;

    @FXML
    private JFXComboBox<SerialBaudRate> cbSerialSpeed;

    @FXML
    private JFXTextField tfSerialDataBits;

    @FXML
    private JFXComboBox<SerialStopBits> cbSerialStopBits;

    @FXML
    private JFXComboBox<SerialParity> cbSerialParity;

    @FXML
    private JFXComboBox cbSerialFlowControl;

    @FXML
    private Pane pConnType;

    @FXML
    private JFXButton btnSetFlowControl;

    @FXML
    private StackPane dialogStackMisc;

    @FXML
    private StackPane stackPaneRoot;

    @FXML
    private JFXTextField tfSelectedDisplay;

    @FXML
    private JFXButton btnSelectEmulator;

    @FXML
    private JFXListView<GlcdDisplay> lvEmulators;

    @FXML
    private JFXButton btnSerialPortRefresh;

    @FXML
    private JFXComboBox<SerialPort> cbSerialPorts;

    @FXML
    private JFXCheckBox cbFlowControlRts;

    @FXML
    private JFXCheckBox cbFlowControlCts;

    @FXML
    private JFXCheckBox cbFlowControlDtr;

    @FXML
    private JFXCheckBox cbFlowControlDsr;

    @FXML
    private JFXCheckBox cbFlowControlXIn;

    @FXML
    private JFXCheckBox cbFlowControlXOut;

    @FXML
    private JFXTextField tfFlowControl;

    @FXML
    private JFXTextArea taLog;

    @FXML
    private StatusBar statusBar;

    @FXML
    private JFXComboBox<GlcdBusInterface> cbBusInterface;

    @FXML
    private MenuItem menuAbout;

    @FXML
    private MenuItem menuReportIssue;

    @FXML
    private JFXButton btnDeveloper;
    //</editor-fold>

    private final DateTimeFormatter imageFileNameFormatter = DateTimeFormatter.ofPattern("YYYYMMddkkmmss'_GlcdCapture'");

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private final DecimalFormat decimalFormatter = new DecimalFormat("##.00");

    private final AtomicReference<GlcdEmulatorProfile> selectedProfileContext = new AtomicReference<>(null);

    private final AtomicBoolean modifierCtrlPressed = new AtomicBoolean(false);

    private GlcdConfigApp appConfig = getContext().getAppConfig();

    private BindGroup appBindGroup = new BindGroup();

    private BindGroup profileBindGroup = new BindGroup();

    private FadeTransition screenshotTransition;

    private ObjectProperty<PixelBuffer> displayBuffer = new SimpleObjectProperty<>();

    private ScannerService scannerService = new ScannerService();

    private SerialPortService serialPortService = new SerialPortService();

    private DisplayService displayService;

    private DrawTestService drawTestService;

    private final FilteredList<GlcdBusInterface> busInterfaceList = FXCollections.observableArrayList(GlcdBusInterface.values()).filtered(p -> true);

    //<editor-fold desc="String Converters">
    private StringConverter<SerialPort> serialPortStringConverter = new StringConverter<SerialPort>() {
        @Override
        public String toString(SerialPort object) {
            return object.getSystemPortName();
        }

        @Override
        public SerialPort fromString(String string) {
            if (serialPortService.getSerialPorts().size() > 0) {
                return serialPortService.getSerialPorts()
                        .stream()
                        .filter(p -> p.getSystemPortName().equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(null);
            }
            return null;
        }
    };

    private StringConverter<SerialBaudRate> baudRateStringConverter = new StringConverter<SerialBaudRate>() {
        SerialBaudRate lastConverted = null;

        @Override
        public String toString(SerialBaudRate object) {
            lastConverted = object;
            return String.valueOf(object.toValue());
        }

        @Override
        public SerialBaudRate fromString(String string) {
            if (!StringUtils.isNumeric(string))
                return lastConverted;
            return SerialBaudRate.fromValue(Integer.parseInt(string));
        }
    };

    private StringConverter<SerialParity> parityStringConverter = new StringConverter<SerialParity>() {
        @Override
        public String toString(SerialParity object) {
            return StringUtils.capitalize(object.name().toLowerCase()) + " - " + String.valueOf(object.toValue());
        }

        @Override
        public SerialParity fromString(String string) {
            return SerialParity.fromValue(Integer.parseInt(string));
        }
    };

    private StringConverter<SerialStopBits> stopBitsStringConverter = new StringConverter<SerialStopBits>() {
        private SerialStopBits lastConverted;

        @Override
        public String toString(SerialStopBits object) {
            lastConverted = object;
            return String.valueOf(object.toValue());
        }

        @Override
        public SerialStopBits fromString(String string) {
            if (!StringUtils.isNumeric(string))
                return lastConverted;
            return SerialStopBits.fromValue(Integer.parseInt(string));
        }
    };
    //</editor-fold>

    @FunctionalInterface
    @Deprecated
    private interface CommandNoArg {
        void execute();
    }

    private EventHandler<KeyEvent> profileTableKeyEventHandler = event -> {
        if (event.getCode() == KeyCode.ENTER) {
            profileLoadSelected();
        } else if (event.getCode() == KeyCode.DELETE) {
            profileDeleteAction(null);
        }
    };

    private EventHandler<WindowEvent> windowCloseFilter = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            if (event.isConsumed())
                return;
            log.debug("Close Event Filter from: Controller (Source: {}, Target: {})", event.getSource(), event.getTarget());
            profileCheckModified(!appConfig.isConfirmOnExit(), !appConfig.isRememberSettingsOnExit());
            if (appConfig.isRememberSettingsOnExit()) {
                saveAppSettings();
            }

            if (displayService.isRunning()) {
                if (displayService.isClientConnected()) {
                    if (!DialogUtil.promptConfirmation("A client is currently connected", "Emulator service is still running and a client is currently connected, are you sure you want to quit?")) {
                        event.consume();
                        return;
                    }
                }
                stopDisplayService();
            }
        }
    };

    private void setupDisplayScreen() {
        attachAutoFitWindowBindings(glcdScreen.widthProperty());
        attachAutoFitWindowBindings(glcdScreen.heightProperty());
        //glcdScreen.setShowFPS(true);
    }

    @Override
    public void initializeOnce() {
        OutputStream os = new TextAreaOutputStream(taLog);
        GlcdOutputStreamAppender.setStaticOutputStream(os);

        screenshotTransition = new FadeTransition(Duration.millis(500), glcdScreen);
        screenshotTransition.setFromValue(0.0);
        screenshotTransition.setToValue(1.0);
        screenshotTransition.setAutoReverse(false);
        screenshotTransition.setCycleCount(1);
        screenshotTransition.setInterpolator(Interpolator.EASE_BOTH);

        //Listen invalidation changes in active profile property
        getContext().getProfileManager().activeProfileProperty().addListener((observable, oldValue, newValue) -> {
            log.info("Profile switched from '{}' to '{}'", oldValue, newValue);
            activateProfile(newValue);
        });

        appConfig.defaultProfileIdProperty().addListener((observable, oldValue, newValue) -> log.info("Default profile changed from {} to {}", oldValue, newValue));

        btnDeveloper.setVisible(appConfig.isDeveloperMode());
        btnDeveloper.setDisable(!appConfig.isDeveloperMode());
        btnDeveloper.setOnAction(this::showDeveloperWindow);

        setupNodeProperties(); //must be called first
        setupDisplayScreen();
        setupDefaultProfile();
        setupConnectionTypeBindings();
        setupDisplayService();
        setupDrawTestService();
        setupDisabledProperties();
        setupStatusBar();
        updateAppBindings(appConfig);
        applyTheme(appConfig.getThemeId());

        Context.getPrimaryStage().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowCloseFilter);

        if (appConfig.isRunEmulatorAtStartup()) {
            startDisplayService();
        }
    }

    private void showDeveloperWindow(ActionEvent event) {
        Stages.getDeveloperStage().showAndWait();
    }

    private void setupStatusBar() {
        GlcdEmulatorProfile profile = getContext().getProfileManager().getActiveProfile();

        Label displaySize = new Label();
        displaySize.setTextAlignment(TextAlignment.CENTER);
        displaySize.textProperty().bind(Bindings.format("%d x %d", profile.displaySizeWidthProperty(), profile.displaySizeHeightProperty()));

        Label pixelSize = new Label();
        pixelSize.textProperty().bind(Bindings.format("%.2f", profile.lcdPixelSizeProperty()));

        Label screenFpsLabel = new Label("Render FPS: 0");
        screenFpsLabel.textProperty().bind(Bindings.format("Screen FPS: %d", glcdScreen.fpsCountProperty()));

        StatusIndicator emulatorStatusIndicator = new StatusIndicator();
        emulatorStatusIndicator.activatedProperty().bind(displayService.runningProperty());
        StatusIndicator clientConnectionStatus = new StatusIndicator();
        clientConnectionStatus.activatedProperty().bind(displayService.clientConnectedProperty());

        statusBar.setPadding(new Insets(5, 5, 5, 5));

        ObservableList<Node> leftItems = statusBar.getLeftItems();
        ObservableList<Node> rightItems = statusBar.getRightItems();

        leftItems.clear();
        rightItems.clear();

        rightItems.add(displaySize);
        rightItems.add(new Separator(Orientation.VERTICAL));
        rightItems.add(screenFpsLabel);
        rightItems.add(new Separator(Orientation.VERTICAL));
        rightItems.add(pixelSize);

        Label emulatorFps = new Label("FPS: 0");
        Label emulatorBps = new Label("0 B/s");
        Label emulatorFrameSize = new Label("Frame Size: 0");
        Label connectionType = new Label("Connection: N/A");
        connectionType.textProperty().bind(Bindings.format("Connection: %s", appConfig.connectionTypeProperty()));
        Label connectionDetails = new Label("");

        StringBinding connectionDetailsBinding = Bindings.createStringBinding(() -> {
            if (displayService.isRunning()) {
                String details = "";
                switch (displayService.getConnectionType()) {
                    case TCP:
                        details = String.format("IP: %s, Port: %d", appConfig.getListenIp(), appConfig.getListenPort());
                        break;
                    case SERIAL:
                        details = String.format("Port: %s, Baud Rate: %d", appConfig.getSerialPortName(), appConfig.getSerialBaudRate().toValue());
                        break;
                }
                return details;
            }
            return StringUtils.EMPTY;
        }, displayService.runningProperty());
        connectionDetails.textProperty().bind(connectionDetailsBinding);

        //Only bind if a task is available
        displayService.displayTaskProperty().addListener((observable, oldTask, newTask) -> {
            if (newTask != null) {
                emulatorFps.textProperty().bind(Bindings.format("FPS: %d", displayService.getStatistics().fpsCounterProperty()));
                emulatorBps.textProperty().bind(Bindings.format("%.2f KB/s", displayService.getStatistics().bpsCountProperty().divide(1024.0f)));
                emulatorFrameSize.textProperty().bind(Bindings.format("Frame Size: %d B", displayService.getStatistics().frameSizeProperty()));
            }
        });

        leftItems.add(emulatorStatusIndicator);
        leftItems.add(new Label("Emulator"));
        leftItems.add(new Separator(Orientation.VERTICAL));
        leftItems.add(clientConnectionStatus);
        leftItems.add(new Label("Client"));
        leftItems.add(new Separator(Orientation.VERTICAL));
        leftItems.add(emulatorFps);
        leftItems.add(new Separator(Orientation.VERTICAL));
        leftItems.add(emulatorBps);
        leftItems.add(new Separator(Orientation.VERTICAL));
        leftItems.add(emulatorFrameSize);
        leftItems.add(new Separator(Orientation.VERTICAL));
        leftItems.add(connectionType);
        leftItems.add(new Separator(Orientation.VERTICAL));
        leftItems.add(connectionDetails);
    }

    private void disableConnectionTypeOptions(boolean disable) {
        pConnType.setDisable(disable);
        for (Toggle toggle : connType.getToggles()) {
            ToggleButton button = (ToggleButton) toggle;
            button.setDisable(disable);
        }
    }

    private void activateProfile(GlcdEmulatorProfile profile) {
        glcdScreen.stop();
        try {
            updateProfileBindings(profile);
            //Refresh the display buffer
            refreshDisplayBuffer();
            log.debug("Profile '{}' activated", profile);
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

        log.info("Display buffer refreshed (Width: {}, Height: {})", activeProfile.getDisplaySizeWidth(), activeProfile.getDisplaySizeHeight());
    }

    private void handleDrawTestAction(ActionEvent event) {
        if (!drawTestService.isRunning()) {
            drawTestService.restart();
        } else {
            drawTestService.cancel();
        }
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

    /**
     * Check if we have a default profile stored in the File System.
     */
    private void setupDefaultProfile() {
        //Load default profile
        int defaultProfileId = appConfig.getDefaultProfileId();
        ProfileManager profileManager = getContext().getProfileManager();
        GlcdEmulatorProfile defaultProfile = profileManager.getProfile(defaultProfileId);

        if (defaultProfile == null) {
            log.info("No default profile found in file system for id '{}', using default", defaultProfileId);
            defaultProfile = new GlcdEmulatorProfile();
            appConfig.setDefaultProfile(defaultProfile);
            profileManager.getProfiles().add(defaultProfile);
            log.info("Setting new default profile to '{}'", defaultProfile);
        }
        profileManager.setActiveProfile(defaultProfile);
        log.info("Loaded default profile: {}", defaultProfile);
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

    private void updateDefaultProfile(GlcdEmulatorProfile profile) {
        if (profile != null && !appConfig.isDefault(profile)) {
            String header = "Are you sure you want to set '" + profile.getName() + "' as the default profile?";
            String content = "The selected profile will be applied on the next application start";
            if (DialogUtil.promptConfirmation(header, content)) {
                appConfig.setDefaultProfile(profile);
            }
        }
    }

    private ListenerOptions createListenerOptions() {
        ListenerOptions options = new ListenerOptions();
        switch (appConfig.getConnectionType()) {
            case TCP:
                options.put(TcpListenerOptions.IP_ADDRESS, appConfig.getListenIp());
                options.put(TcpListenerOptions.PORT_NUMBER, appConfig.getListenPort());
                break;
            case SERIAL:
                options.put(SerialListenerOptions.SERIAL_PORT_NAME, appConfig.getSerialPortName());
                options.put(SerialListenerOptions.BAUD_RATE, appConfig.getSerialBaudRate().toValue());
                options.put(SerialListenerOptions.PARITY, appConfig.getSerialParity().toValue());
                options.put(SerialListenerOptions.DATA_BITS, appConfig.getSerialDataBits());
                options.put(SerialListenerOptions.FLOW_CONTROL, appConfig.getSerialFlowControl());
                options.put(SerialListenerOptions.STOP_BITS, appConfig.getSerialStopBits().toValue());
                options.put(SerialListenerOptions.PORT_SERVICE, serialPortService);
                break;
        }
        return options;
    }

    private void updateListenerOptions() {
        if (displayService != null) {
            displayService.setConnectionOptions(createListenerOptions());
            log.debug("Listener options updated");
        }
    }

    private void setupDisabledProperties() {
        BooleanBinding disabledBinding = displayService.runningProperty().or(drawTestService.runningProperty());

        btnDrawAnimTest.disableProperty().bind(displayService.runningProperty());
        tbListen.disableProperty().bind(drawTestService.runningProperty());
        menuEmulatorControl.disableProperty().bind(drawTestService.runningProperty());

        btnFreezeDisplay.disableProperty().bind(disabledBinding.not());
        sizePane.disableProperty().bind(disabledBinding);
        apProfiles.disableProperty().bind(disabledBinding);
        btnReset.disableProperty().bind(disabledBinding);
        tfSelectedDisplay.disableProperty().bind(disabledBinding);
        btnSelectEmulator.disableProperty().bind(disabledBinding);
        btnClearDisplay.disableProperty().bind(disabledBinding);
        cbBusInterface.disableProperty().bind(disabledBinding.or(appConfig.serviceModeProperty().isEqualTo(ServiceMode.SIMULATED)));
        pConnType.disableProperty().bind(disabledBinding);
        rbConnTypeSerial.disableProperty().bind(disabledBinding);
        rbConnTypeTcp.disableProperty().bind(disabledBinding);
        btnShowFontBrowser.disableProperty().bind(disabledBinding);
    }

    private void setupDisplayService() {
        if (displayService != null)
            return;

        log.debug("START: Setup display service");

        GlcdEmulatorProfile profile = getContext().getProfileManager().getActiveProfile();

        displayService = new DisplayService();
        displayService.displayProperty().bindBidirectional(profile.displayProperty());
        displayService.busInterfaceProperty().bindBidirectional(profile.busInterfaceProperty());
        displayService.bufferProperty().bindBidirectional(this.displayBuffer);
        displayService.serviceModeProperty().bindBidirectional(appConfig.serviceModeProperty());
        displayService.setByteProcessor(ByteProcessorFactory.create(appConfig.getServiceMode()));
        menuEmulatorControl.textProperty().bind(Bindings.createStringBinding(() -> {
            menuEmulatorControl.setUserData(displayService.isRunning());
            if (displayService.isRunning()) {
                return "Stop Service";
            }
            return "Start Service";
        }, displayService.runningProperty()));

        menuEmulatorControl.setOnAction(event -> {
            MenuItem menu = (MenuItem) event.getSource();
            boolean started = (boolean) menu.getUserData();
            if (started) {
                if (displayService.isRunning() && displayService.isClientConnected()) {
                    if (!DialogUtil.promptConfirmation("Are you sure you want to stop the service?", "A client is currently connected to the emulator service.")) {
                        log.info("Cancelled stop service");
                        return;
                    }
                }
                stopDisplayService();
            } else {
                startDisplayService();
            }
        });

        displayService.runningProperty().addListener((observable, oldValue, newValue) -> tbListen.setSelected(newValue));
        tbListen.textProperty().bind(Bindings.createStringBinding(this::tbListenTextStringBinding, displayService.runningProperty()));
        tbListen.addEventFilter(MouseEvent.MOUSE_PRESSED, this::tbListenFilterMousePress);
        tbListen.setOnAction(this::tbListenActionEventHandler);

        log.debug("END: Setup display service");
    }

    private String tbListenTextStringBinding() {
        if (displayService.isRunning()) {
            return "STOP";
        }
        return "START";
    }

    private void tbListenActionEventHandler(ActionEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (button.isSelected()) {
            if (!displayService.isRunning()) {
                startDisplayService();
            }
        } else {
            if (displayService.isRunning()) {
                stopDisplayService();
            }
        }
    }

    private <T extends Event> void tbListenFilterMousePress(T event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (displayService.isRunning() && displayService.isClientConnected() && button.isSelected()) {
            if (!DialogUtil.promptConfirmation("Are you sure you want to stop the service?", "A client is currently connected to the emulator service.")) {
                event.consume();
            } else {
                button.setSelected(false);
                EventUtil.fireEvent(tbListen, new ActionEvent(button, button));
            }
        }
    }

    /**
     * Starts the emulator listen service
     */
    private void startDisplayService() {
        ObservableList<Stage> stages = StageHelper.getStages();
        for (Stage s : stages) {
            if ("U8G2 Font Browser".equals(s.getTitle()) && s.isShowing()) {
                DialogUtil.showInfo("Cannot start service", "You cannot start the service while the font browser is open, please close the browser first and try again");
                return;
            }
        }
        try {
            //Update listener optins
            updateListenerOptions();
            displayService.restart();
        } catch (Exception e) {
            tbListen.setSelected(false);
            throw new EmulatorControllerException("Could not start emulator service", e);
        }
    }

    private void handleTaskMessages(ObservableValue<? extends String> observableValue, String oldMsg, String newMsg) {
        taLog.appendText(newMsg + "\n");
        taLog.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Stops the emulator listen service
     */
    private boolean stopDisplayService() {
        return displayService.cancel();
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

    private JFXDialog getEmulatorBrowserDialog() {
        try {
            Parent node = ResourceUtil.loadFxmlResource(Views.EMULATOR_BROWSE, GlcdEmulatorController.this);

            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Label("Select the display controller for the current profile"));
            content.setBody(node);
            content.setPrefWidth(500);
            content.setPrefHeight(400);
            JFXButton btnSelect = new JFXButton("Select");
            JFXButton btnCancel = new JFXButton("Cancel");
            JFXButton btnRefresh = new JFXButton("Refresh");
            btnRefresh.disableProperty().bind(scannerService.runningProperty());
            content.setActions(btnSelect, btnCancel, btnRefresh);

            JFXDialog emulatorBrowseDialog = new JFXDialog(stackPaneRoot, content, JFXDialog.DialogTransition.RIGHT);
            btnRefresh.setOnAction(e -> {
                lvEmulators.getItems().clear();
                scannerService.setFilter(ScannerService.FILTER_NONE);
                scannerService.setForceRefresh(true);
                scannerService.restart();
            });

            btnCancel.setOnAction(e -> emulatorBrowseDialog.close());
            btnSelect.setOnAction(ev -> {
                GlcdEmulatorProfile profile = getContext().getProfileManager().getActiveProfile();
                GlcdDisplay selectedDisplay = lvEmulators.getSelectionModel().getSelectedItem();
                String displayController = selectedDisplay.getController().name() + " - " + selectedDisplay.getName();
                log.info("Selected display controller: {}", displayController);
                profile.setDisplay(selectedDisplay);
                log.info("Updating display with/height properties based on '{}'", displayController);
                profile.setDisplaySizeWidth(selectedDisplay.getDisplaySize().getDisplayWidth());
                profile.setDisplaySizeHeight(selectedDisplay.getDisplaySize().getDisplayHeight());
                fitWindowToScreen();
                emulatorBrowseDialog.close();
            });

            lvEmulators.setCellFactory(new Callback<ListView<GlcdDisplay>, ListCell<GlcdDisplay>>() {
                @Override
                public ListCell<GlcdDisplay> call(ListView<GlcdDisplay> param) {
                    return new ListCell<GlcdDisplay>() {
                        @Override
                        protected void updateItem(GlcdDisplay display, boolean empty) {
                            super.updateItem(display, empty);
                            Platform.runLater(() -> {
                                if (empty) {
                                    setText(null);
                                } else {
                                    setText(String.format("%s - %s (%dx%d)", display.getController().name(), display.getName().replaceAll("D_", ""), display.getDisplaySize().getDisplayWidth(), display.getDisplaySize().getDisplayHeight()));
                                }
                            });
                        }
                    };
                }
            });

            scannerService.setOnSucceeded(event -> {
                if (scannerService.getValue() != null) {
                    lvEmulators.setItems(scannerService.getValue());
                    GlcdEmulatorProfile profile = getContext().getProfileManager().getActiveProfile();
                    if (lvEmulators.getItems().isEmpty())
                        return;
                    if (lvEmulators.getItems().size() == 1)
                        lvEmulators.getSelectionModel().selectFirst();
                    else {
                        lvEmulators.getSelectionModel().select(profile.getDisplay());
                    }

                }
            });

            emulatorBrowseDialog.setOnDialogOpened(event1 -> scannerService.restart());

            return emulatorBrowseDialog;
        } catch (IOException e) {
            throw new EmulatorControllerException(e);
        }
    }

    private void setupDrawTestService() {
        GlcdEmulatorProfile profile = getContext().getProfileManager().getActiveProfile();
        drawTestService = new DrawTestService();

        ObjectBinding<GlcdBusInterface> busInterfaceBinding = Bindings.createObjectBinding(() -> {
            GlcdBusInterface busInterface = profile.getBusInterface();
            if (busInterface == null) {
                busInterface = profile.getDisplay().getBusInterfaces().stream().findFirst().orElse(null);
                if (busInterface != null)
                    log.info("Bus interface is null, returning default: {}", busInterface.name());
                else
                    log.warn("Bus interface not found for display: {}", profile.getDisplay().getName());
            }
            return busInterface;
        }, profile.busInterfaceProperty());

        StringBinding textBinding = Bindings.createStringBinding(() -> {
            if (drawTestService.isRunning()) {
                return "Stop Draw Test";
            }
            return "Start Draw Test";
        }, drawTestService.runningProperty());

        drawTestService.bufferProperty().bind(displayBuffer);
        drawTestService.busInterfaceProperty().bind(busInterfaceBinding);
        drawTestService.displayProperty().bind(profile.displayProperty());
        btnDrawAnimTest.textProperty().bind(textBinding);
    }

    /**
     * Configure node properties, static entries, event handlers, action handlers etc.
     */
    private void setupNodeProperties() {
        btnDrawAnimTest.setOnAction(this::handleDrawTestAction);

        //Disable if not in developer mode
        spnDisplayWidth.setDisable(!appConfig.isDeveloperMode());
        spnDisplayHeight.setDisable(!appConfig.isDeveloperMode());


        cbBusInterface.setItems(busInterfaceList);
        btnFreezeDisplay.setOnAction(event -> {
            if (glcdScreen.isRunning()) {
                glcdScreen.stop();
            } else {
                glcdScreen.start();
            }
        });

        btnFreezeDisplay.textProperty().bind(Bindings.createStringBinding(() -> {
            if (glcdScreen.isRunning()) {
                btnFreezeDisplay.setStyle(null);
                return "Freeze";
            }
            btnFreezeDisplay.setStyle("-fx-background-color: #6b879c; -fx-text-fill: black");
            return "Un-Freeze";
        }, glcdScreen.runningProperty()));

        btnClearDisplay.setOnAction(event -> {
            if (displayBuffer.get() != null)
                displayBuffer.get().clear();
        });


        btnSelectEmulator.setOnAction(event -> {
            JFXDialog emulatorBrowserDialog = getEmulatorBrowserDialog();
            emulatorBrowserDialog.show();
        });

        btnSaveSettings.setOnAction(event -> {
            List<GlcdEmulatorProfile> modifiedProfiles = profileGetModified();
            if (!modifiedProfiles.isEmpty())
                profileSaveAll(modifiedProfiles);
            saveAppSettings();
        });
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

        menuOpenFontBrowser.setOnAction(this::openFontBrowserAction);
        menuExit.setOnAction(event -> {
            Stage stage = Context.getPrimaryStage();
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        menuAbout.setOnAction(event -> {
            Stage aboutStage = Stages.getAboutStage();
            aboutStage.setResizable(false);
            aboutStage.showAndWait();
        });
        menuCheckUpdates.setOnAction(event -> Context.getInstance().getHostServices().showDocument(Common.RELEASE_URL));
        menuReportIssue.setOnAction(event -> Context.getInstance().getHostServices().showDocument(Common.REPORT_ISSUE_URL));
        menuSaveScreen.setOnAction(event -> saveScreenCapture());
        menuSaveScreenAs.setOnAction(event -> saveScreenCaptureAs(appConfig.getLastSavedImagePath(), appConfig::setLastSavedImagePath));
        menuSaveSettings.setOnAction(event -> saveAppSettings());
        btnReset.setOnAction(this::resetToDefaultSettings);
        btnDonate.setOnAction(event -> Platform.runLater(() -> Context.getInstance().getHostServices().showDocument(Common.DONATE_URL)));
        btnOpenScreenshotPath.setOnAction(createOpenDirPathEventHandler("Select screenshot directory", tfScreenshotPath.textProperty()));
        btnOpenProfileDirPath.setOnAction(createOpenDirPathEventHandler("Select profile directory", tfProfileDirPath.textProperty()));
        btnFitScreenToWindow.setOnAction(noArgEventHandler(this::fitWindowToScreen));
        menuFitScreenToWindow.setOnAction(noArgEventHandler(this::fitWindowToScreen));
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

        setupIntegerSpinner(spnDisplayWidth, 8, 400, 8);
        setupIntegerSpinner(spnDisplayHeight, 8, 400, 8);
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

    private void setupConnectionTypeBindings() {

        rbConnTypeTcp.setUserData(ConnectionType.TCP);
        rbConnTypeSerial.setUserData(ConnectionType.SERIAL);

        connType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            try {
                Parent view = null;
                ConnectionType type = (ConnectionType) newValue.getUserData();
                switch (type) {
                    case TCP:
                        view = ResourceUtil.loadFxmlResource("emulator-settings-tcp", GlcdEmulatorController.this);
                        setupConnTypeTcpBindings();
                        break;
                    case SERIAL:
                        view = ResourceUtil.loadFxmlResource("emulator-settings-serial", GlcdEmulatorController.this);
                        setupConnTypeSerialBindings();
                        break;
                }
                if (view != null) {
                    pConnType.getChildren().clear();
                    pConnType.getChildren().add(view);
                }
            } catch (IOException e) {
                log.error("Could not load settings view", e);
            }
        });

        BindUtil.bindToggleGroupToProperty(connType, appConfig.connectionTypeProperty());
    }

    private void setupConnTypeTcpBindings() {
        setupIntegerSpinner(spnListenPort, 0, 65535, 1);
        Bindings.bindBidirectional(spnListenPort.getValueFactory().valueProperty(), appConfig.listenPortProperty());
        Bindings.bindBidirectional(tfListenIp.textProperty(), appConfig.listenIpProperty());
        btnTcpListenTest.setOnAction(event -> {
            int port = spnListenPort.getValue();
            log.debug("Checking port availability for {}", port);
            if (NetUtils.portAvailable(port))
                DialogUtil.showInfo("Port is available", "Port is " + port + " available");
            else
                DialogUtil.showInfo("Port not available", "Port " + port + " is NOT available");
        });
        btnTcpGetIP.setOnAction(event -> {
            log.debug("Obtaining host IP address");
            tfListenIp.setText(NetUtils.getLocalAddress("127.0.0.1"));
        });
    }

    private Parent createFlowControlView() {
        try {
            return ResourceUtil.loadFxmlResource("emulator-settings-serial-fc", this);
        } catch (IOException e) {
            throw new EmulatorControllerException("Problem loading Flow Control view", e);
        }
    }

    private void setupFlowControlBindinigs() {
        bindFlowControlCheckbox(cbFlowControlRts, SerialFlowControl.RTS);
        bindFlowControlCheckbox(cbFlowControlCts, SerialFlowControl.CTS);
        bindFlowControlCheckbox(cbFlowControlDtr, SerialFlowControl.DTR);
        bindFlowControlCheckbox(cbFlowControlDsr, SerialFlowControl.DSR);
        bindFlowControlCheckbox(cbFlowControlXIn, SerialFlowControl.XONXOFF_IN);
        bindFlowControlCheckbox(cbFlowControlXOut, SerialFlowControl.XONXOFF_OUT);
    }

    private void bindFlowControlCheckbox(CheckBox checkBox, SerialFlowControl data) {
        checkBox.setUserData(data);

        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Property property = (Property) observable;
            CheckBox checkBox1 = (CheckBox) property.getBean();
            SerialFlowControl selectedFc = (SerialFlowControl) checkBox1.getUserData();

            if (SerialFlowControl.NONE.equals(selectedFc)) {
                appConfig.setSerialFlowControl(0);
                return;
            }

            int flowControl = appConfig.getSerialFlowControl();
            if (newValue) {
                flowControl |= selectedFc.toValue();
            } else {
                flowControl &= ~selectedFc.toValue();
            }

            appConfig.setSerialFlowControl(flowControl);
        });

        //Update value
        updateFcCheckboxItems(checkBox);
    }

    private void updateFcCheckboxItems(CheckBox checkBox) {
        SerialFlowControl fc = (SerialFlowControl) checkBox.getUserData();
        if (SerialFlowControl.NONE.equals(fc)) {
            checkBox.setSelected(true);
            return;
        }
        checkBox.setSelected(fc.isSet(appConfig.getSerialFlowControl()));
    }

    private void setupConnTypeSerialBindings() {
        tfFlowControl.textProperty().bind(Bindings.createStringBinding(() -> {
            List<SerialFlowControl> setFc = new ArrayList<>();
            for (SerialFlowControl fc : SerialFlowControl.values()) {
                if (fc.isSet(appConfig.getSerialFlowControl())) {
                    setFc.add(fc);
                }
            }
            if (setFc.isEmpty())
                return "Disabled";
            return StringUtils.join(setFc.toArray(), "/");
        }, appConfig.serialFlowControlProperty()));
        serialPortService.setOnSucceeded(e -> {
            if (serialPortService.getValue() != null && serialPortService.getValue().size() == 1)
                cbSerialPorts.getSelectionModel().selectFirst();
        });

        appConfig.serialPortNameProperty().bind(Bindings.createStringBinding(() -> {
            SerialPort selectedItem = cbSerialPorts.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                log.debug("Selected serial port name");
                return cbSerialPorts.getSelectionModel().getSelectedItem().getSystemPortName();
            }
            return null;
        }, cbSerialPorts.getSelectionModel().selectedItemProperty()));
        cbSerialPorts.itemsProperty().bind(serialPortService.serialPortsProperty());
        btnSerialPortRefresh.setOnAction(this::refreshSerialPorts);
        cbSerialPorts.setConverter(serialPortStringConverter);
        cbSerialSpeed.setItems(FXCollections.observableArrayList(SerialBaudRate.values()));
        cbSerialParity.setItems(FXCollections.observableArrayList(SerialParity.values()));
        cbSerialStopBits.setItems(FXCollections.observableArrayList(SerialStopBits.values()));
        cbSerialSpeed.setConverter(baudRateStringConverter);
        cbSerialParity.setConverter(parityStringConverter);
        cbSerialStopBits.setConverter(stopBitsStringConverter);
        btnSetFlowControl.setOnAction(event -> {
            JFXDialogLayout content = new JFXDialogLayout();
            content.setHeading(new Label("Flow Control Options"));
            content.setBody(createFlowControlView());
            content.setPrefWidth(474);
            content.setPrefHeight(120);
            JFXButton btnOk = new JFXButton("Okay");
            content.setActions(btnOk);
            JFXDialog dialog = new JFXDialog(stackPaneRoot, content, JFXDialog.DialogTransition.CENTER);
            btnOk.setOnAction(ev -> dialog.close());
            setupFlowControlBindinigs();
            dialog.show();
        });
        Bindings.bindBidirectional(tfSerialDataBits.textProperty(), appConfig.serialDataBitsProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(cbSerialSpeed.valueProperty(), appConfig.serialBaudRateProperty());
        Bindings.bindBidirectional(cbSerialParity.valueProperty(), appConfig.serialParityProperty());
        Bindings.bindBidirectional(cbSerialStopBits.valueProperty(), appConfig.serialStopBitsProperty());

        if (serialPortService.getSerialPorts().size() == 0)
            refreshSerialPorts(null);

        serialPortService.serialPortsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.size() > 0) {
                for (SerialPort port : cbSerialPorts.getItems()) {
                    if (port.getSystemPortName().equalsIgnoreCase(appConfig.getSerialPortName())) {
                        cbSerialPorts.getSelectionModel().select(port);
                        return;
                    }
                }
            }
        });


        if (cbSerialPorts.getItems().size() == 1) {
            cbSerialPorts.getSelectionModel().selectFirst();
        } else {
            selectSerialPortByName(appConfig.getSerialPortName());
        }

        //cbSerialPorts.getSelectionModel().select(serialPortService.findSerialPortByName(appConfig.getSerialPortName()));
    }

    private void selectSerialPortByName(String name) {
        if (StringUtils.isBlank(name))
            return;
        for (SerialPort port : cbSerialPorts.getItems()) {
            if (name.equalsIgnoreCase(port.getSystemPortName())) {
                cbSerialPorts.getSelectionModel().select(port);
            }
        }
    }

    private void refreshSerialPorts(ActionEvent event) {
        serialPortService.restart();
    }

    private void resetToDefaultSettings(ActionEvent event) {
        if (DialogUtil.promptConfirmation("Reset active profile to default settings?", "")) {
            ProfileManager profileManager = getContext().getProfileManager();
            GlcdEmulatorProfile activeProfile = profileManager.getActiveProfile();

            //Retain active profile's identity, everything else should reset to default
            GlcdEmulatorProfile newProfile = new GlcdEmulatorProfile();
            newProfile.setId(activeProfile.getId());
            newProfile.setFile(activeProfile.getFile());
            newProfile.setName(activeProfile.getName());

            profileManager.delete(activeProfile);
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

        appBindGroup.registerUnidirectional(bpGlcd.topProperty(), createNodeVisibilityBinding(hbPins, appConfig.showPinActivityPaneProperty()));
        appBindGroup.registerUnidirectional(bpGlcd.bottomProperty(), createNodeVisibilityBinding(tpSettings, appConfig.showSettingsPaneProperty()));
        appBindGroup.registerUnidirectional(tvProfiles.itemsProperty(), getContext().getProfileManager().filteredProfilesProperty());
        appBindGroup.registerUnidirectional(getContext().getProfileManager().filterProperty(), createFilterObjectBinding());

        //appBindGroup.registerBidirectional(cbServiceMode.valueProperty(), appConfig.serviceModeProperty());
        appBindGroup.registerBidirectional(displayService.connectionTypeProperty(), appConfig.connectionTypeProperty());
        appBindGroup.registerBidirectional(menuShowToolbar.selectedProperty(), appConfig.toolbarVisibleProperty());
        appBindGroup.registerBidirectional(menuAlwaysOnTop.selectedProperty(), appConfig.alwaysOnTopProperty());
        appBindGroup.registerBidirectional(tfProfileDirPath.textProperty(), appConfig.profileDirPathProperty());
        appBindGroup.registerBidirectional(cbRunEmulatorStartup.selectedProperty(), appConfig.runEmulatorAtStartupProperty());
        appBindGroup.registerBidirectional(cbAutoSaveSettings.selectedProperty(), appConfig.rememberSettingsOnExitProperty());
        appBindGroup.registerBidirectional(cbConfirmExit.selectedProperty(), appConfig.confirmOnExitProperty());
        appBindGroup.registerBidirectional(cbFitWindowToScreen.selectedProperty(), appConfig.autoFitWindowToScreenProperty());
        appBindGroup.registerBidirectional(tfScreenshotPath.textProperty(), appConfig.screenshotDirPathProperty());

        appBindGroup.registerBidirectional(menuSettings.selectedProperty(), appConfig.showSettingsPaneProperty());
        appBindGroup.registerBidirectional(menuPinActivity.selectedProperty(), appConfig.showPinActivityPaneProperty());
        appBindGroup.registerBidirectional(btnShowSettings.selectedProperty(), appConfig.showSettingsPaneProperty());
        appBindGroup.registerBidirectional(btnShowPinActivity.selectedProperty(), appConfig.showPinActivityPaneProperty());

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
     * Register profile bindings. This can be safely called multiple times in case the reference of the profile model changes
     *
     * @param profile
     *         The {@link GlcdEmulatorProfile} instance to apply the bindings to
     */
    private void updateProfileBindings(GlcdEmulatorProfile profile) {
        if (profile == null)
            return;

        log.debug("START: Profile bindings update");

        //Clear binds (If exists)
        profileBindGroup.clear();

        //Initialize selected bus interface item,
        //this needs to be called first prior to calling
        // bind otherwise the value will be null
        updateBusInterfaceSelection();

        //Unidirectional Bindings
        profile.displaySizeWidthProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Resizing Pixel Buffer: Old Width = {}, New Width = {}", oldValue, newValue);
            displayBuffer.get().resize(newValue, null);
        });
        profile.displaySizeHeightProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Resizing Pixel Buffer: Old Height = {}, New Height = {}", oldValue, newValue);
            displayBuffer.get().resize(null, newValue);
        });

        cbBusInterface.setConverter(new StringConverter<GlcdBusInterface>() {
            @Override
            public String toString(GlcdBusInterface object) {
                return object.getDescription();
            }

            @Override
            public GlcdBusInterface fromString(String string) {
                return GlcdBusInterface.valueOf(string);
            }
        });

        ObjectBinding<Predicate<? super GlcdBusInterface>> busPredicateBinding = Bindings.createObjectBinding(
                () -> busInterface -> {
                    if (profile.getDisplay() != null) {
                        List<GlcdBusInterface> buses = profile.getDisplay().getBusInterfaces();
                        return buses.contains(busInterface) && GlcdBusType.SOFTWARE.equals(busInterface.getBusType());
                    }
                    return false;
                }, profile.displayProperty()
        );

        profile.displayProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                updateBusInterfaceSelection();
        });

        profileBindGroup.registerUnidirectional(busInterfaceList.predicateProperty(), busPredicateBinding);
        profileBindGroup.registerUnidirectional(profile.busInterfaceProperty(), cbBusInterface.getSelectionModel().selectedItemProperty());


        profileBindGroup.registerUnidirectional(tfSelectedDisplay.textProperty(), Bindings.createStringBinding(() -> {
            GlcdDisplay display = profile.getDisplay();
            if (display == null) {
                return "None";
            }
            return display.getController().name() + " - " + display.getName();
        }, profile.displayProperty()));

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

        profile.displaySizeWidthProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Validating display width change");
            int maxWidth = profile.getDisplay().getDisplaySize().getDisplayWidth();
            if (newValue > maxWidth) {
                String msg = String.format("The display width set is greater than the maximum width of the selected display controller. (Controller: %s, Max Width = %d Actual Width = %d)", profile.getDisplay().getController().name() + "::" + profile.getDisplay().getName(), maxWidth, newValue);
                log.warn("WIDTH RESIZE WARNING: {}", msg);
                if (!appConfig.isSkipResizeWarning()) {
                    Alert diag = DialogUtil.createAlertDialogWithCheckbox(Alert.AlertType.WARNING, "Warning", "", msg, "Ok, don't bug me next time", appConfig::setSkipResizeWarning, ButtonType.OK);
                    diag.showAndWait();
                }
            }
        });

        profile.displaySizeHeightProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Validating display height change");
            int maxHeight = profile.getDisplay().getDisplaySize().getDisplayHeight();
            if (newValue > maxHeight) {
                String msg = String.format("The display height set is greater than the maximum width of the selected display controller. (Controller: %s, Max Width = %d Actual Width = %d)", profile.getDisplay().getController().name() + "::" + profile.getDisplay().getName(), maxHeight, newValue);
                log.warn("HEIGHT RESIZE WARNING: {}", msg);
                if (!appConfig.isSkipResizeWarning()) {
                    Alert diag = DialogUtil.createAlertDialogWithCheckbox(Alert.AlertType.WARNING, "Warning", "", msg, "Ok, don't bug me next time", appConfig::setSkipResizeWarning, ButtonType.OK);
                    diag.showAndWait();
                }
            }
        });

        profileBindGroup.registerBidirectional(cbPixelShape.valueProperty(), profile.lcdPixelShapeProperty());

        profileBindGroup.bind();

        log.debug("END: Profile bindings update");
    }

    private void updateBusInterfaceSelection() {
        GlcdEmulatorProfile profile = getContext().getProfileManager().getActiveProfile();
        GlcdDisplay display = profile.getDisplay();

        if (display == null) {
            log.debug("Display not available");
            return;
        }

        GlcdBusInterface pBusInterface = profile.getBusInterface();
        //select default bus if null or the selected bus is not currently on the list
        if (pBusInterface == null || !busInterfaceList.contains(pBusInterface)) {
            Class<? extends GlcdEmulator> emulator = GlcdUtil.findEmulatorClass(profile.getDisplay());
            if (emulator != null) {
                pBusInterface = emulator.getAnnotation(Emulator.class).defaultBus();
                log.debug("Bus interface not specified. Using default '{}'", pBusInterface);
            }
        }
        cbBusInterface.getSelectionModel().select(pBusInterface);
    }

    private ObjectBinding<Node> createNodeVisibilityBinding(Node showNode, ObservableValue<Boolean> criteriaProperty) {
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
        return getContext().getProfileManager().findList(ProfileManager.Predicates.modified());
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
        if (displayService.isRunning() || drawTestService.isRunning()) {
            DialogUtil.showInfo("Action not allowed", "You cannot use the font browser while the emulator/test service is running due to stability issues. Please stop the service first and try again");
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
        ProfileManager profileManager = getContext().getProfileManager();
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
                getContext().getThemeManager().applyToAll();
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
            double height = glcdScreen.getHeight() + statusBar.getHeight() + toolbarHeight + mbMain.getHeight() + (stage.getHeight() - stage.getScene().getHeight()) + settingsPaneHeight + padding;
            double width = glcdScreen.getWidth() + padding;
            if (bpGlcd.getTop() != null && bpGlcd.getTop().equals(hbPins)) {
                height += hbPins.getHeight();
            }
            stage.setWidth(width);
            stage.setHeight(height);
        });
    }

    private EventHandler<ActionEvent> noArgEventHandler(CommandNoArg commandNoArg) {
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

    private void saveScreenCaptureAs(String initialDir, Consumer<String> saveLastPathTo) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        fileChooser.setInitialFileName(imageFileNameFormatter.format(ZonedDateTime.now()));
        if (!StringUtils.isBlank(initialDir)) {
            fileChooser.setInitialDirectory(new File(initialDir));
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG File", "png"));
        File file = fileChooser.showSaveDialog(Context.getPrimaryStage());
        saveScreenCapture(file);
        if (file != null) {
            saveLastPathTo.accept(FilenameUtils.getFullPath(file.getAbsolutePath()));
        }
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
            log.info("Saved screenshot '{}' to directory '{}'", imageFile.getName(), FilenameUtils.getFullPath(imageFile.getAbsolutePath()));
        }
    }
}
