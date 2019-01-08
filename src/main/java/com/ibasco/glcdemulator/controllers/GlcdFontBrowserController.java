/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdFontBrowserController.java
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

import com.ibasco.glcdemulator.Controller;
import com.ibasco.glcdemulator.Controllers;
import com.ibasco.glcdemulator.constants.Views;
import com.ibasco.glcdemulator.controls.GlcdScreen;
import com.ibasco.glcdemulator.enums.PixelShape;
import com.ibasco.glcdemulator.model.FontCacheDetails;
import com.ibasco.glcdemulator.model.FontCacheEntry;
import com.ibasco.glcdemulator.services.FontCacheService;
import com.ibasco.glcdemulator.utils.*;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdSize;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlcdFontBrowserController extends Controller implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GlcdFontBrowserController.class);

    //<editor-fold desc="FXML Properties">
    @FXML
    private GlcdScreen fontDisplay;

    @FXML
    private JFXTextField tfPreviewText;

    @FXML
    private JFXSpinner pbLoadFonts;

    @FXML
    private Label lblLoadFonts;

    @FXML
    private StackPane spFontCacheProgress;

    @FXML
    private AnchorPane apFontBrowser;

    @FXML
    private JFXHamburger hamLeftDrawer;

    @FXML
    private VBox root;

    @FXML
    private JFXButton btnReloadCache;

    @FXML
    private JFXTextField tfFontNameCpp;

    @FXML
    private JFXTextField tfFontNameJava;

    @FXML
    private JFXTextField tfFontMaxWidth;

    @FXML
    private JFXTextField tfFontMaxHeight;

    @FXML
    private JFXTextField tfFontAscent;

    @FXML
    private JFXTextField tfFontDescent;

    @FXML
    private JFXComboBox<FontSizeInfo> cbFontSize;

    @FXML
    private JFXTextField tfFontName;

    @FXML
    private JFXRadioButton rbFilterSize;

    @FXML
    private JFXRadioButton rbFilterName;

    @FXML
    private JFXButton btnCopyU8g2;

    @FXML
    private JFXButton btnCopyJava;

    @FXML
    private JFXCheckBox cbFilterSize;

    @FXML
    private JFXCheckBox cbFilterName;

    @FXML
    private JFXComboBox<GlcdSize> cbDisplaySize;

    @FXML
    private Label lblTotalFonts;
    //</editor-fold>

    private static final String DEFAULT_TEXT = "ABC def 123";

    private FontRenderer fontRenderer = FontRenderer.getInstance();

    private FontCacheService fontCacheService = new FontCacheService();

    private FontCacheDetails details = new FontCacheDetails();

    private JFXDrawersStack drawersStack;

    private JFXDrawer drawer;

    private HamburgerSlideCloseTransition transition;

    private final AtomicBoolean modifierCtrlPressed = new AtomicBoolean(false);

    public FontCacheDetails getDetails() {
        return details;
    }

    public void setDetails(FontCacheDetails details) {
        this.details = details;
    }

    private class FontSizeInfo {
        private FontCacheEntry entry;

        private FontSizeInfo(FontCacheEntry entry) {
            this.entry = entry;
        }

        private int getArea() {
            return entry.getMaxCharWidth() * entry.getMaxCharHeight();
        }

        private int getWidth() {
            return entry.getMaxCharWidth();
        }

        private int getHeight() {
            return entry.getMaxCharHeight();
        }

        private String getName() {
            return entry.getMaxCharWidth() + " x " + entry.getMaxCharHeight();
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("INITIALIZE: {}", fontDisplay);
        initFontDisplay();
        initDrawer();
        initPreloaderBindings();
        initPreviewTextBindings();
        btnReloadCache.setOnAction(this::rebuildCache);
        btnCopyU8g2.setOnAction(this::copyU8g2Name);
        btnCopyJava.setOnAction(this::copyJavaName);
        cbFontSize.disableProperty().bind(cbFilterSize.selectedProperty().not());
        tfFontName.disableProperty().bind(cbFilterName.selectedProperty().not());
        cbDisplaySize.setConverter(new StringConverter<GlcdSize>() {
            @Override
            public String toString(GlcdSize object) {
                return object.getDisplayWidth() + " x " + object.getDisplayHeight();
            }

            @Override
            public GlcdSize fromString(String string) {
                return null;
            }
        });
        ObservableList<GlcdSize> sizeList = FXCollections.observableArrayList(GlcdSize.values());
        sizeList.sort(Comparator.comparing(GlcdSize::getDisplayWidth, Integer::compareTo));
        cbDisplaySize.setItems(sizeList);
        cbDisplaySize.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<GlcdDisplay> res = GlcdUtil.findDisplay(GlcdUtil.bySize(newValue));
                if (!res.isEmpty()) {
                    GlcdDisplay display = res.get(0);
                    log.debug("Choosing display size: {}, Selected display = {}", newValue, display);
                    fontRenderer.setDisplay(display);
                    //re-initialize the font display screen
                    initFontDisplay();
                }
            }
        });

        //Lazy-initialization
        root.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) //noinspection Duplicates
            {
                drawersStack = (JFXDrawersStack) newValue.getRoot();
                drawersStack.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.CONTROL)
                        modifierCtrlPressed.set(true);
                });
                drawersStack.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
                    if (event.getCode() == KeyCode.CONTROL)
                        modifierCtrlPressed.set(false);
                });
            }
        });

        fontCacheService.entriesProperty().bind(details.entriesProperty());

        //Start retrieving cached entries
        if (fontCacheService.getState().equals(Worker.State.READY))
            fontCacheService.start();

        fontCacheService.setOnSucceeded(event -> {
            cbFontSize.setItems(extractSizeInfoList(fontCacheService.getValue()));
            GlcdFontDrawerController drawerController = Controllers.getController(GlcdFontDrawerController.class);
            drawerController.selectFirst();
        });

        cbFilterSize.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbFilterName.selectedProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        cbFontSize.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        tfFontName.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        updateFilters();
    }

    private void updateFilters() {
        Predicate<FontCacheEntry> p = e -> true;
        if (cbFilterSize.isSelected() && !cbFontSize.getSelectionModel().isEmpty()) {
            FontSizeInfo sizeInfo = cbFontSize.getSelectionModel().getSelectedItem();
            log.debug("Updating font size filter: {}", sizeInfo);
            p = p.and(e -> e.getMaxCharWidth() == sizeInfo.getWidth() && e.getMaxCharHeight() == sizeInfo.getHeight());
        }
        if (cbFilterName.isSelected() && !StringUtils.isBlank(tfFontName.getText())) {
            String criteriaText = tfFontName.getText().toLowerCase();
            p = p.and(e -> e.getFont().getKey().toLowerCase().contains(criteriaText) || e.getFont().name().toLowerCase().contains(criteriaText));
        }

        details.getFilteredEntries().setPredicate(p);
        ((GlcdFontDrawerController) Controllers.getController(GlcdFontDrawerController.class)).selectFirst();
        log.debug("Filtered count = {}", details.getFilteredEntries().size());
        lblTotalFonts.setText(String.valueOf(details.getFilteredEntries().size()));
    }

    @SuppressWarnings("Duplicates")
    private void resizeLcdOnScroll(ScrollEvent event) {
        boolean forwardScroll = event.getDeltaY() >= 0;
        if (!modifierCtrlPressed.get()) {
            GlcdFontDrawerController drawerController = Controllers.getController(GlcdFontDrawerController.class);
            if (!forwardScroll) {
                drawerController.selectNextFont();
            } else {
                drawerController.selectPreviousFont();
            }
            return;
        }
        double value = fontDisplay.getPixelSize() + (forwardScroll ? 0.2 : -0.2);
        if (value > 0.39) {
            fontDisplay.setPixelSize(value);
            fontDisplay.refresh();
        }
    }

    private ObservableList<FontSizeInfo> extractSizeInfoList(ObservableList<FontCacheEntry> entries) {
        return entries.stream()
                .filter(f -> f.getMaxCharWidth() != 0 && f.getMaxCharHeight() != 0)
                .filter(distinctByKey((Function<FontCacheEntry, String>) fontCacheEntry -> fontCacheEntry.getMaxCharWidth() + String.valueOf(fontCacheEntry.getMaxCharHeight())))
                .sorted(Comparator.comparing(fontCacheEntry -> fontCacheEntry.getMaxCharWidth() * fontCacheEntry.getMaxCharHeight(), Integer::compareTo))
                .map(FontSizeInfo::new)
                .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private void copyJavaName(ActionEvent actionEvent) {
        ClipboardContent content = new ClipboardContent();
        content.putString(tfFontNameJava.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    private void copyU8g2Name(ActionEvent actionEvent) {
        ClipboardContent content = new ClipboardContent();
        content.putString(tfFontNameCpp.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    private void rebuildCache(ActionEvent actionEvent) {
        if (DialogUtil.promptConfirmation("Are you sure you want to rebuild the font cache?", "Rebuilding the whole cache may take a while")) {
            showDrawer(false);
            Platform.runLater(() -> fontCacheService.rebuild());
        }
    }

    //<editor-fold desc="Initialize Properties and Bindings">
    private void initPreviewTextBindings() {
        details.previewTextProperty().bindBidirectional(tfPreviewText.textProperty());
        //Render text on new font selection
        details.activeEntryProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //log.debug("ACTIVE ENTRY CHANGED: {}", fontDisplay.getBuffer());
                renderText(newValue.getFont(), tfPreviewText.getText());
                tfFontNameCpp.setText(newValue.getFont().getKey());
                tfFontNameJava.setText(newValue.getFont().name());
                tfFontAscent.setText(String.valueOf(newValue.getAscent()));
                tfFontDescent.setText(String.valueOf(newValue.getDescent()));
                tfFontMaxWidth.setText(String.valueOf(newValue.getMaxCharWidth()));
                tfFontMaxHeight.setText(String.valueOf(newValue.getMaxCharHeight()));
            }
        });
        //Render text if preview text changes
        tfPreviewText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (details.getActiveEntry() == null) {
                log.warn("No selected font, no text has been rendered");
                return;
            }
            renderText(details.getActiveEntry().getFont(), newValue);
        });
        tfPreviewText.setText(DEFAULT_TEXT);
    }

    private void initDrawer() {
        try {
            Controllers.initialize(GlcdFontDrawerController.class, details);

            VBox drawerPane = ResourceUtil.loadFxmlResource(Views.FONT_BROWSER_TOPDRAWER);

            drawer = new JFXDrawer();
            drawer.setSidePane(drawerPane);
            drawer.setDirection(JFXDrawer.DrawerDirection.TOP);
            drawer.setDefaultDrawerSize(150);
            drawer.setResizeContent(true);
            drawer.setOverLayVisible(false);
            drawer.setResizableOnDrag(false);
            drawer.setId("TOP");

            transition = new HamburgerSlideCloseTransition(hamLeftDrawer);
            hamLeftDrawer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> toggleDrawer());
        } catch (IOException e) {
            log.error("There was a problem loading the view for '" + Views.FONT_BROWSER_TOPDRAWER + "'", e);
        }
    }

    private void initFontDisplay() {
        fontDisplay.setBuffer(new PixelBuffer(fontRenderer.getDriver().getWidth(), fontRenderer.getDriver().getHeight()));
        fontDisplay.setBacklightColor(Color.BLACK); //79ff4d
        fontDisplay.setActivePixelColor(Color.WHITE);
        fontDisplay.setInactivePixelColor(Color.WHITE);
        fontDisplay.setDropShadowVisible(true);
        fontDisplay.setContrast(5.0f);
        fontDisplay.setPixelSize(4.5f);
        fontDisplay.setSpacing(0.0);
        fontDisplay.setMargin(20.0);
        fontDisplay.setPixelShape(PixelShape.RECTANGLE);
        fontDisplay.setOnScroll(this::resizeLcdOnScroll);
    }

    private void initPreloaderBindings() {
        pbLoadFonts.progressProperty().bind(fontCacheService.progressProperty());
        BooleanBinding showProgressBinding = Bindings.createBooleanBinding(() -> Worker.State.RUNNING.equals(fontCacheService.getState()), fontCacheService.stateProperty());

        //Show these properties once the service is on RUNNING state
        lblLoadFonts.visibleProperty().bind(showProgressBinding);
        pbLoadFonts.visibleProperty().bind(showProgressBinding);
        spFontCacheProgress.visibleProperty().bind(showProgressBinding);

        lblLoadFonts.textProperty().bind(Bindings.createStringBinding(() -> String.format("Caching fonts... (%d/%d)", (int) fontCacheService.getWorkDone(), (int) fontCacheService.getTotalWork()), fontCacheService.progressProperty()));
        apFontBrowser.effectProperty().bind(Bindings.createObjectBinding((Callable<Effect>) () -> {
            GaussianBlur blur = new GaussianBlur(25.75);
            if (Worker.State.RUNNING.equals(fontCacheService.getState())) {
                return blur;
            }
            return null;
        }, showProgressBinding));
    }
    //</editor-fold>

    private void toggleDrawer() {
        showDrawer(!drawer.isOpened());
    }

    private void showDrawer(boolean show) {
        transition.setRate(show ? 1 : -1);
        transition.play();
        drawersStack.toggle(drawer, show);
    }

    private void renderText(GlcdFont font, String text) {
        /*if (fontCacheService.isRunning()) {
            log.debug("Font cache service is currently running, skipping render.");
            return;
        }*/
        fontRenderer.renderFont(fontDisplay, font, text);
        drawFontHeader(font, fontDisplay);
    }

    private void drawFontHeader(GlcdFont font, GlcdScreen display) {
        GraphicsContext gc = display.getGraphicsContext2D();
        gc.setFont(new Font("Consolas", 10));
        gc.setFill(Color.DARKGRAY);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        String text = font.name() + " (" + font.getKey() + ")";
        gc.fillText(text, Math.round(display.getWidth() / 2), 13);
    }
}
