/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
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
import com.ibasco.glcdemulator.constants.Views;
import com.ibasco.glcdemulator.controls.GlcdScreen;
import com.ibasco.glcdemulator.enums.PixelShape;
import com.ibasco.glcdemulator.model.FontCacheDetails;
import com.ibasco.glcdemulator.services.FontCacheService;
import com.ibasco.glcdemulator.utils.DialogUtil;
import com.ibasco.glcdemulator.utils.FontRenderer;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.glcdemulator.utils.ResourceUtil;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class GlcdFontBrowserController extends Controller implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GlcdFontBrowserController.class);

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
    private StackPane spFontDetails;

    @FXML
    private JFXHamburger hamLeftDrawer;

    @FXML
    private VBox root;

    @FXML
    private JFXButton btnReloadCache;

    private static final String DEFAULT_TEXT = "ABC def 123";

    private FontRenderer fontRenderer;

    private FontCacheService fontCacheService;

    private FontCacheDetails details;

    private JFXDrawersStack drawersStack;

    private JFXDrawer leftDrawer;

    private HamburgerSlideCloseTransition transition;

    public GlcdFontBrowserController(FontCacheService service, FontCacheDetails details) {
        this.fontCacheService = service;
        this.details = details;
    }

    public FontCacheService getFontCacheService() {
        return fontCacheService;
    }

    public void setFontCacheService(FontCacheService fontCacheService) {
        this.fontCacheService = fontCacheService;
    }

    public FontCacheDetails getDetails() {
        return details;
    }

    public void setDetails(FontCacheDetails details) {
        this.details = details;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initFontDisplay();
        initDrawer();
        initPreloaderBindings();
        initPreviewTextBindings();

        btnReloadCache.setOnAction(event -> {
            if (DialogUtil.promptConfirmation("Are you sure you want to rebuild the font cache?", "Rebuilding the whole cache may take a while")) {
                showTopDrawer(false);
                Platform.runLater(() -> fontCacheService.rebuild());
            }
        });

        //Lazy-initialization
        root.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                drawersStack = (JFXDrawersStack) newValue.getRoot();
                //Context.getInstance().getThemeManager().applyTheme(newValue);
            }
        });

        //Start retrieving cached entries
        if (fontCacheService.getState().equals(Worker.State.READY))
            fontCacheService.start();

        //Show the left drawer once the cache is made available
        fontCacheService.setOnSucceeded(event -> {
            /*Platform.runLater(() -> {
                showTopDrawer(true);
            });*/
        });
    }

    //<editor-fold desc="Initialize Properties and Bindings">
    private void initPreviewTextBindings() {
        details.previewTextProperty().bindBidirectional(tfPreviewText.textProperty());
        //Render text on new font selection
        details.activeEntryProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                renderText(newValue.getFont(), tfPreviewText.getText());
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
            GlcdFontTopDrawerController leftDrawerController = new GlcdFontTopDrawerController(fontCacheService, details);
            VBox leftDrawerPane = ResourceUtil.loadFxmlResource(Views.FONT_BROWSER_TOPDRAWER, leftDrawerController);
            leftDrawer = new JFXDrawer();
            leftDrawer.setSidePane(leftDrawerPane);
            leftDrawer.setDirection(JFXDrawer.DrawerDirection.TOP);
            leftDrawer.setDefaultDrawerSize(150);
            leftDrawer.setResizeContent(true);
            leftDrawer.setOverLayVisible(false);
            leftDrawer.setResizableOnDrag(false);
            leftDrawer.setId("TOP");

            transition = new HamburgerSlideCloseTransition(hamLeftDrawer);
            hamLeftDrawer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> toggleTopDrawer());
        } catch (IOException e) {
            log.error("There was a problem loading the view for '" + Views.FONT_BROWSER_TOPDRAWER + "'", e);
        }
    }

    private void initFontDisplay() {
        fontRenderer = FontRenderer.getInstance();
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

    private void toggleTopDrawer() {
        showTopDrawer(!leftDrawer.isOpened());
    }

    private void showTopDrawer(boolean show) {
        transition.setRate(show ? 1 : -1);
        transition.play();
        drawersStack.toggle(leftDrawer, show);
    }

    private void renderText(GlcdFont font, String text) {
        if (fontCacheService.isRunning()) {
            log.debug("Font cache service is currently running, skipping render.");
            return;
        }
        fontRenderer.renderFont(fontDisplay, font, text);
        fontDisplay.refresh();
        drawFontHeader(font, fontDisplay);
    }

    private void drawFontHeader(GlcdFont font, GlcdScreen display) {
        GraphicsContext gc = display.getGraphicsContext2D();
        gc.setFont(new Font("Verdana", 10));
        gc.setFill(Color.DARKGRAY);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        String text = font.name() + " (" + font.getKey() + ")";
        gc.fillText(text, Math.round(display.getWidth() / 2), 13);
    }
}
