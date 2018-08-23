package com.ibasco.glcdemu.controllers;

import com.ibasco.glcdemu.GlcdController;
import com.ibasco.glcdemu.GlcdProfileManager;
import com.ibasco.glcdemu.constants.Common;
import com.ibasco.glcdemu.controls.GlcdScreen;
import com.ibasco.glcdemu.enums.PixelShape;
import com.ibasco.glcdemu.model.GlcdConfigApp;
import com.ibasco.glcdemu.model.GlcdEmulatorProfile;
import com.ibasco.glcdemu.utils.BindGroup;
import com.ibasco.glcdemu.utils.FileUtils;
import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.*;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    public MenuBar mbMain;

    @FXML
    public MenuItem menuLoadSettings;

    @FXML
    public MenuItem menuSaveSettings;

    @FXML
    private MenuItem menuSaveScreen;

    @FXML
    private MenuItem menuSaveScreenAs;

    @FXML
    private MenuItem menuExit;

    @FXML
    private CheckMenuItem menuAlwaysOnTop;

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
    private Button btnFitScreenToWindow;

    @FXML
    private ToggleButton btnShowPinActivity;

    @FXML
    private ToggleButton btnShowSettings;

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
    private TabPane tpSettings;

    @FXML
    private ColorPicker cpBacklight;

    @FXML
    private ColorPicker cpInactivePixel;

    @FXML
    private ColorPicker cpActivePixel;

    @FXML
    private Button btnSaveSettings;

    @FXML
    private Slider slPixelSize;

    @FXML
    private Button btnApplySettings;

    @FXML
    private Button btnReset;

    @FXML
    private TextField tfProfileFilter;

    @FXML
    private Button btnNewProfile;

    @FXML
    private Button btnMakeDefault;

    @FXML
    private TextField tfProfileDirPath;

    @FXML
    private Button btnOpenProfileDirPath;

    @FXML
    private CheckBox cbConfirmExit;

    @FXML
    private CheckBox cbAutoSaveSettings;

    @FXML
    private TextField tfScreenshotPath;

    @FXML
    private Button btnOpenScreenshotPath;

    @FXML
    private CheckBox cbFitWindowToScreen;

    @FXML
    private Spinner<Integer> spnDisplayWidth;

    @FXML
    private Spinner<Integer> spnDisplayHeight;

    @FXML
    private TextField tfListenIp;

    @FXML
    private Spinner<Integer> spnListenPort;

    @FXML
    private TableView tvLog;

    @FXML
    private HBox hbStatusBar;

    @FXML
    private Button btnToolOpenSettings;

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
    private Slider slContrast;

    @FXML
    private Slider slMargin;

    @FXML
    private Slider slPixelSpacing;

    @FXML
    private ChoiceBox<PixelShape> cbPixelShape;
    //</editor-fold>

    private final DateTimeFormatter imageFileNameFormatter = DateTimeFormatter.ofPattern("YYYYMMddkkmmss'_GlcdCapture'");

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    private final AtomicReference<GlcdEmulatorProfile> selectedProfileContext = new AtomicReference<>(null);

    private final AtomicBoolean modifierCtrlPressed = new AtomicBoolean(false);

    private GlcdConfigApp appConfig = getContext().getAppConfig();

    private BindGroup appBindGroup = new BindGroup();

    private BindGroup profileBindGroup = new BindGroup();

    private DecimalFormat decimalFormatter = new DecimalFormat("##.00");

    public GlcdEmulatorController(Stage stage) {
        super(stage);
    }

    @FunctionalInterface
    private interface CommandNoArg {
        void execute();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Listen invalidation changes in active profile property
        getContext().getProfileManager().activeProfileProperty().addListener((observable, oldValue, newValue) -> {
            log.debug("Active profile changed from '{}' to '{}'", oldValue, newValue);
            updateProfileBindings(newValue);
        });
        getContext().appConfigProperty().addListener(new ChangeListener<GlcdConfigApp>() {
            @Override
            public void changed(ObservableValue<? extends GlcdConfigApp> observable, GlcdConfigApp oldValue, GlcdConfigApp newValue) {
                log.debug("App config instance has changed: {}", newValue);
            }
        });
        appConfig.defaultProfileIdProperty().addListener((observable, oldValue, newValue) -> log.info("Default profile changed from {} to {}", oldValue, newValue));
        attachAutoFitWindowBindings(glcdScreen.widthProperty());
        attachAutoFitWindowBindings(glcdScreen.heightProperty());
    }

    private void attachAutoFitWindowBindings(ObservableValue<Number> numberProperty) {
        numberProperty.addListener((observable, oldValue, newValue) -> {
            if (!stage.isMaximized() && appConfig.isAutoFitWindowToScreen()) {
                fitWindowToScreen();
            }
        });
    }

    @Override
    public void onInit() {
        setupNodeProperties();
        applyDefaultProfile();
        updateProfileBindings(getContext().getProfileManager().getActiveProfile());
        updateAppBindings(appConfig);
        applyTheme(appConfig.getThemeId(), stage);
    }

    private void buildProfileContextMenu() {
        GlcdProfileManager profileManager = getContext().getProfileManager();
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

    private void profileEditAction(ActionEvent event) {
        try {
            GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.stage);
            stage.setTitle("Edit Profile");
            GlcdEditProfileController controller = new GlcdEditProfileController(stage);
            Parent root = ResourceUtil.loadFxmlResource("edit-profile-dialog", controller);
            if (root == null)
                return;
            controller.setProfile(selectedProfile);
            stage.setScene(new Scene(root));
            applyTheme(appConfig.getThemeId(), stage);
            controller.onInit();
            stage.showAndWait();
        } catch (IOException e) {
            log.error("Error during edit", e);
        }
    }

    private void profileDeleteAction(ActionEvent event) {
        //GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
        BooleanProperty deleteProfileInFS = new SimpleBooleanProperty(false);
        Alert confirmDeleteAlert = createAlertDialogWithCheckbox(Alert.AlertType.CONFIRMATION,
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

    private Alert createAlertDialogWithCheckbox(Alert.AlertType type, String title, String header, String content, String optOutMessage, Consumer<Boolean> optOutAction, ButtonType... buttonTypes) {
        Alert alert = new Alert(type);
        // Need to force the alert to layout in order to grab the graphic,
        // as we are replacing the dialog pane with a custom pane
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();
        alert.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                CheckBox optOut = new CheckBox();
                optOut.setText(optOutMessage);
                optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
                return optOut;
            }
        });
        alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setGraphic(graphic);
        alert.getDialogPane().setExpanded(true);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

    private void setupProfileTable() {
        buildProfileContextMenu();
        tvProfiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tvProfiles.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tvProfiles.setOnKeyReleased(event -> {
            GlcdProfileManager profileManager = getContext().getProfileManager();
            if (event.getCode() == KeyCode.ENTER) {
                GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
                profileManager.setActiveProfile(selectedProfile);
            } else if (event.getCode() == KeyCode.DELETE) {
                profileDeleteAction(null);
            }
        });
        tvProfiles.setOnMouseClicked(event -> {
            GlcdProfileManager profileManager = getContext().getProfileManager();
            if (event.getClickCount() == 2) {
                GlcdEmulatorProfile selectedProfile = tvProfiles.getSelectionModel().getSelectedItem();
                profileManager.setActiveProfile(selectedProfile);
            }
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

        tvProfiles.getColumns().setAll(idCol, nameCol, descCol, displayWidthCol, displayHeightCol, pixelSizeCol, activeCol, defaultCol, fileCol);
    }

    private void updateDefaultProfile(GlcdEmulatorProfile profile) {
        if (profile != null && !appConfig.isDefault(profile)) {
            Alert confirmSetDefault = new Alert(Alert.AlertType.CONFIRMATION, "The selected profile will be applied on the next application start", ButtonType.YES, ButtonType.NO);
            confirmSetDefault.setTitle("Confirm Set Default Profile");
            confirmSetDefault.setHeaderText("Are you sure you want to set '" + profile.getName() + "' as the default profile?");
            Optional<ButtonType> response = confirmSetDefault.showAndWait();
            if (response.isPresent() && response.get() == ButtonType.YES) {
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

    /**
     * Configure node properties once
     */
    private void setupNodeProperties() {
        menuExit.setOnAction(event -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        menuSaveScreen.setOnAction(event -> saveScreenCapture());
        menuLoadSettings.setOnAction(this::openSettingsFileAction);
        btnReset.setOnAction(event -> getContext().getProfileManager().setActiveProfile(new GlcdEmulatorProfile()));
        btnToolOpenSettings.setOnAction(this::openSettingsFileAction);
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

    private void updateAppBindings(GlcdConfigApp appConfig) {
        if (appConfig == null)
            return;

        appBindGroup.clear();
        appBindGroup.registerUnidirectional(hbGlcd.prefWidthProperty(), Bindings.subtract(scpGlcd.widthProperty(), 3));
        appBindGroup.registerUnidirectional(hbGlcd.prefHeightProperty(), Bindings.subtract(scpGlcd.heightProperty(), 3));
        appBindGroup.registerUnidirectional(bpGlcd.topProperty(), createNodeToBorderPaneBinding(hbPins, appConfig.showPinActivityPaneProperty()));
        appBindGroup.registerUnidirectional(bpGlcd.bottomProperty(), createNodeToBorderPaneBinding(tpSettings, appConfig.showSettingsPaneProperty()));
        appBindGroup.registerUnidirectional(tvProfiles.itemsProperty(), getContext().getProfileManager().filteredProfilesProperty());
        appBindGroup.registerUnidirectional(getContext().getProfileManager().filterProperty(), createFilterObjectBinding());

        appBindGroup.registerBidirectional(menuAlwaysOnTop.selectedProperty(), appConfig.alwaysOnTopProperty());
        appBindGroup.registerBidirectional(tfProfileDirPath.textProperty(), appConfig.profileDirPathProperty());
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
        appBindGroup.attach();
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
     * @param profile The {@link GlcdEmulatorProfile} instance to apply the bindings to
     */
    private void updateProfileBindings(GlcdEmulatorProfile profile) {
        if (profile == null)
            return;

        //Clear binds (If exists)
        profileBindGroup.clear();

        //Unidirectional Bindings
        profileBindGroup.registerUnidirectional(lblStatus.textProperty(), Bindings.format("Active Profile = %s", profile.nameProperty()));
        profileBindGroup.registerUnidirectional(glcdScreen.displayWidthProperty(), profile.displaySizeWidthProperty());
        profileBindGroup.registerUnidirectional(glcdScreen.displayHeightProperty(), profile.displaySizeHeightProperty());

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

        profileBindGroup.attach();
    }

    private ObjectBinding<Node> createNodeToBorderPaneBinding(Node showNode, ObservableValue<Boolean> criteriaProperty) {
        return Bindings.createObjectBinding(() -> {
            try {
                return criteriaProperty.getValue() ? showNode : null;
            } finally {
                if (appConfig.isAutoFitWindowToScreen())
                    fitWindowToScreen();
            }
        }, criteriaProperty);
    }

    private String createProfileNameFromSettings() {
        return "Glcd_" +
                glcdScreen.getDisplayWidth() +
                "x" +
                glcdScreen.getDisplayHeight() +
                "_" +
                Math.round(getContext().getProfileManager().getActiveProfile().getLcdPixelSize()) +
                "PX";
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

    private void profileCheckModified() {
        List<GlcdEmulatorProfile> modifiedProfiles = getContext().getProfileManager().findList(GlcdProfileManager.Predicates.modified());
        if (modifiedProfiles.isEmpty())
            return;

        boolean alwaysOnTop = stage.isAlwaysOnTop();

        try {
            stage.setAlwaysOnTop(false);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Confirm Save Action");
            alert.setContentText("You have uncommitted changes present, would you like to save them?");
            alert.setHeaderText("Save uncommitted changes?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                for (GlcdEmulatorProfile profile : modifiedProfiles) {
                    getContext().getProfileManager().save(profile);
                    log.debug("Saved Profile: '{}' to '{}'", profile.getName(), profile.getFile());
                }
            }
        } finally {
            stage.setAlwaysOnTop(alwaysOnTop);
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

    private void applyThemeBindings() {
        menuThemes.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String selectedThemeId = Objects.toString(newValue.getUserData(), "");
                log.info("Theme selection changed: {}", selectedThemeId);
                appConfig.setThemeId(selectedThemeId);
            }
        });
        appConfig.themeIdProperty().addListener((observable, oldValue, newValue) -> {
            if (!StringUtils.isBlank(newValue)) {
                applyTheme(newValue, stage);
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

    private void applyTheme(String themeId, Stage stage) {
        if (StringUtils.isBlank(themeId)) {
            log.debug("Theme ID is blank. Nothing to apply: {}", themeId);
            return;
        }

        log.debug("Applying Theme ID: {}", themeId);
        switch (themeId) {
            case "menuThemeDefault":
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(ResourceUtil.getStylesheet("app.css").toExternalForm());
                break;
            case "menuThemeDark":
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(ResourceUtil.getStylesheet("app-dark.css").toExternalForm());
                break;
            default:
                throw new RuntimeException("Invalid theme ID: " + themeId);
        }

        //Update selection if applicable
        selectThemeMenuItem(themeId);
    }

    private File openFileFromDialog(String title, String initDirectory, FileChooser.ExtensionFilter extFilters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (!StringUtils.isBlank(initDirectory)) {
            fileChooser.setInitialDirectory(new File(initDirectory));
        } else {
            fileChooser.setInitialDirectory(new File(Common.USER_DIR));
        }
        fileChooser.getExtensionFilters().add(extFilters);
        File file = fileChooser.showOpenDialog(stage);
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
        return dirChooser.showDialog(stage);
    }

    @Override
    public void onClose() {
        try {
            profileCheckModified();

            if (appConfig.isRememberSettingsOnExit()) {
                getContext().getConfigService().save(appConfig);
                //getContext().getProfileManager().save();
            }
        } catch (IOException e) {
            log.error("Unable to save settings", e);
        }
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
        File file = fileChooser.showSaveDialog(stage);
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
            int pixelSize = (int) glcdScreen.getPixelSize();
            WritableImage wim = new WritableImage((int) glcdScreen.getWidth(), (int) glcdScreen.getHeight());
            glcdScreen.snapshot(null, wim);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", imageFile);
                log.info("Saved file to : {}", FilenameUtils.getFullPath(imageFile.getAbsolutePath()));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
