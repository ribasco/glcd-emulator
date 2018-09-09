package com.ibasco.glcdemu.controllers;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.GlcdController;
import com.ibasco.glcdemu.constants.Views;
import com.ibasco.glcdemu.controls.GlcdScreen;
import com.ibasco.glcdemu.enums.PixelShape;
import com.ibasco.glcdemu.model.FontCacheDetails;
import com.ibasco.glcdemu.model.FontCacheEntry;
import com.ibasco.glcdemu.services.FontCacheService;
import com.ibasco.glcdemu.utils.FontRenderer;
import com.ibasco.glcdemu.utils.PixelBuffer;
import com.ibasco.glcdemu.utils.ResourceUtil;
import com.ibasco.pidisplay.drivers.glcd.enums.GlcdFont;
import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
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
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class GlcdFontBrowserController extends GlcdController implements Initializable {

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
    private JFXListView<FontCacheEntry> lvFonts;

    @FXML
    private JFXHamburger hamLeftDrawer;

    @FXML
    private VBox root;

    @FXML
    private JFXButton btnReloadCache;

    private static final String DEFAULT_TEXT = "ABC def 123";

    private FontRenderer fontRenderer;

    private FontCacheService fontCacheService;

    private DecimalFormat df = new DecimalFormat("#.##");

    private FontCacheDetails details;

    private JFXDrawersStack drawersStack;

    private JFXDrawer leftDrawer;

    public GlcdFontBrowserController(FontCacheService service, FontCacheDetails details) {
        this.fontCacheService = service;
        this.details = details;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("Initializing main font browser");
        initDrawer();

        btnReloadCache.setOnAction(event -> {
            fontCacheService.rebuild();
        });

        //Lazy-initialization
        root.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                drawersStack = (JFXDrawersStack) newValue.getRoot();
                Context.getInstance().getThemeManager().applyTheme(newValue);
            }
        });

        initFontDisplay();
        initPreloaderBindings();
        initPreviewTextBindings();

        //Start retrieving cached entries
        fontCacheService.start();
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
                log.warn("No selected font, skipping render");
                return;
            }
            renderText(details.getActiveEntry().getFont(), newValue);
        });
        tfPreviewText.setText(DEFAULT_TEXT);
    }

    private void initDrawer() {
        try {
            GlcdFontLeftDrawerController leftDrawerController = new GlcdFontLeftDrawerController(fontCacheService, details);
            VBox leftDrawerPane = ResourceUtil.loadFxmlResource(Views.FONT_BROWSER_LEFTDRAWER, leftDrawerController);
            leftDrawer = new JFXDrawer();
            leftDrawer.setSidePane(leftDrawerPane);
            leftDrawer.setDefaultDrawerSize(260);
            leftDrawer.setResizeContent(true);
            leftDrawer.setOverLayVisible(false);
            leftDrawer.setResizableOnDrag(true);
            leftDrawer.setId("LEFT");

            HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamLeftDrawer);
            transition.setRate(-1);
            hamLeftDrawer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                transition.setRate(transition.getRate() * -1);
                transition.play();
                Platform.runLater(() -> drawersStack.toggle(leftDrawer));
            });
            log.debug("Successfully initialized left drawer", leftDrawerPane);
        } catch (IOException e) {
            e.printStackTrace();
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

        lblLoadFonts.textProperty().bind(Bindings.createStringBinding(() -> String.format("Caching fonts...%s%% (%d/%d)", df.format(fontCacheService.getProgress() * 100.0), (int) fontCacheService.getWorkDone(), (int) fontCacheService.getTotalWork()), fontCacheService.progressProperty()));
        apFontBrowser.effectProperty().bind(Bindings.createObjectBinding((Callable<Effect>) () -> {
            GaussianBlur blur = new GaussianBlur(25.75);
            if (Worker.State.RUNNING.equals(fontCacheService.getState())) {
                return blur;
            }
            return null;
        }, showProgressBinding));
    }
    //</editor-fold>

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
