package com.ibasco.glcdemu.controls;

import com.ibasco.glcdemu.enums.PixelShape;
import com.ibasco.glcdemu.utils.NodeUtil;
import com.ibasco.glcdemu.utils.PixelBuffer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings({"unused", "UnnecessaryLocalVariable", "WeakerAccess"})
public class GlcdScreen extends Canvas {

    private static final Logger log = LoggerFactory.getLogger(GlcdScreen.class);

    //<editor-fold desc="Properties">
    private DoubleProperty pixelSize = new SimpleDoubleProperty(5.0);

    private ReadOnlyIntegerWrapper displayWidth = new ReadOnlyIntegerWrapper();

    private ReadOnlyIntegerWrapper displayHeight = new ReadOnlyIntegerWrapper();

    private ObjectProperty<Color> inactivePixelColor = new SimpleObjectProperty<>(Color.web("#000000", 0.0598f));

    private ObjectProperty<Color> activePixelColor = new SimpleObjectProperty<>(Color.web("#000000", 1.0));

    private ObjectProperty<Color> backlightColor = new SimpleObjectProperty<>(Color.web("#a5f242", 1.0));

    private FloatProperty contrast = new SimpleFloatProperty(0.5f);

    private DoubleProperty spacing = new SimpleDoubleProperty(0.0f);

    private DoubleProperty margin = new SimpleDoubleProperty(15.0d);

    private BooleanProperty invalidatedDisplay = new SimpleBooleanProperty(false);

    private BooleanProperty editable = new SimpleBooleanProperty(false);

    private ObjectProperty<PixelShape> pixelShape = new SimpleObjectProperty<>(PixelShape.RECTANGLE);

    private NumberBinding widthBinding = createCanvasResizeBinding(displayWidth);

    private NumberBinding heightBinding = createCanvasResizeBinding(displayHeight);

    private ObjectProperty<PixelBuffer> buffer = new SimpleObjectProperty<>();

    private BooleanProperty dropShadowVisible = new SimpleBooleanProperty(true);

    private StringProperty watermarkText = new SimpleStringProperty();

    private ObjectProperty<Font> watermarkFont = new SimpleObjectProperty<>(new Font("Verdana", 14.0));

    private ObjectProperty<Color> watermarkColor = new SimpleObjectProperty<>(Color.color(0, 0, 0, 0.20d));

    private BooleanProperty showFPS = new SimpleBooleanProperty(false);

    private BooleanProperty autoStart = new SimpleBooleanProperty(false);

    private BooleanProperty gradientBacklight = new SimpleBooleanProperty(true);
    //</editor-fold>

    private DropShadow displayDropShadow;

    private final AtomicBoolean modifierKeyPressed = new AtomicBoolean(false);

    private AtomicBoolean showWatermark = new AtomicBoolean(false);

    private Renderer renderer;

    private final class Renderer extends AnimationTimer {

        private GraphicsContext gc = getGraphicsContext2D();

        private AtomicInteger fpsCounter = new AtomicInteger(0);

        private long prevMillis = 0;

        private static final int interval = 1000000000;

        private Font fpsFont = new Font("Verdana", 18);

        private int fps = 0;

        private BooleanProperty running = new SimpleBooleanProperty(false);

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
            running.set(false);
        }

        @Override
        public void handle(long now) {
            if (!running.get())
                running.set(true);

            if (displayWidth.get() <= 0 || displayHeight.get() <= 0)
                return;

            draw();

            if ((now - prevMillis) > interval) {
                prevMillis = now;
                fps = fpsCounter.getAndSet(0);
            }

            if (isShowFPS()) {
                gc.setFont(fpsFont);
                gc.setFill(backlightColor.get().invert());
                String text = "FPS: " + String.valueOf(fps);
                double height = NodeUtil.computeStringHeight(text, fpsFont);
                gc.fillText(text, margin.get() + pixelSize.get(), height + margin.get());
                fpsCounter.incrementAndGet();
            }
        }

        private boolean isRunning() {
            return running.get();
        }

        public BooleanProperty runningProperty() {
            return running;
        }

        private void setRunning(boolean running) {
            this.running.set(running);
        }
    }

    //<editor-fold desc="Constructor">
    public GlcdScreen() {
        this(false);
    }

    public GlcdScreen(boolean autoStart) {
        setStyle("-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        contrast.addListener((observable, oldValue, newValue) -> {
            float value = (float) newValue / 100f;
            inactivePixelColor.set(updateOpacity(inactivePixelColor.get(), value));
        });

        displayDropShadow = new DropShadow();
        displayDropShadow.setBlurType(BlurType.GAUSSIAN);
        displayDropShadow.setColor(Color.BLACK);
        displayDropShadow.setOffsetX(5.0);
        displayDropShadow.setOffsetY(5.0);
        displayDropShadow.setRadius(35.0);

        effectProperty().bind(Bindings.createObjectBinding((Callable<Effect>) () -> isDropShadowVisible() ? displayDropShadow : null, dropShadowVisible));

        renderer = new Renderer();

        if (autoStart)
            renderer.start();
        else
            Platform.runLater(this::draw);

        //Bind the display width and height as soon as the pixel buffer is made available
        bufferProperty().addListener((observable, oldBuffer, newBuffer) -> {
           /*if (oldBuffer != null && newBuffer == null) {
                displayWidth.unbind();
                displayHeight.unbind();
                widthProperty().unbind();
                heightProperty().unbind();
            }*/

            if (newBuffer != null) {
                displayWidth.bind(newBuffer.widthProperty());
                displayHeight.bind(newBuffer.heightProperty());
                //Bind the canvas width and height properties to automatically
                //adjust when dependent properties have changed
                widthProperty().bind(widthBinding);
                heightProperty().bind(heightBinding);
                draw();
            }
        });
    }
    //</editor-fold>
    public void start() {
        if (!renderer.isRunning())
            renderer.start();
    }

    public void stop() {
        renderer.stop();
    }

    //<editor-fold desc="Property Getter/Setters">
    public boolean isGradientBacklight() {
        return gradientBacklight.get();
    }

    public BooleanProperty gradientBacklightProperty() {
        return gradientBacklight;
    }

    public void setGradientBacklight(boolean gradientBacklight) {
        this.gradientBacklight.set(gradientBacklight);
    }

    private boolean isRunning() {
        return this.renderer.running.get();
    }

    public ReadOnlyBooleanProperty runningProperty() {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(this.renderer.running);
    }

    public boolean isShowFPS() {
        return showFPS.get();
    }

    public BooleanProperty showFPSProperty() {
        return showFPS;
    }

    public void setShowFPS(boolean showFPS) {
        this.showFPS.set(showFPS);
    }

    public Color getWatermarkColor() {
        return watermarkColor.get();
    }

    public ObjectProperty<Color> watermarkColorProperty() {
        return watermarkColor;
    }

    public void setWatermarkColor(Color watermarkColor) {
        this.watermarkColor.set(watermarkColor);
    }

    public Font getWatermarkFont() {
        return watermarkFont.get();
    }

    public ObjectProperty<Font> watermarkFontProperty() {
        return watermarkFont;
    }

    public void setWatermarkFont(Font watermarkFont) {
        this.watermarkFont.set(watermarkFont);
    }

    public String getWatermarkText() {
        return watermarkText.get();
    }

    public StringProperty watermarkTextProperty() {
        return watermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.watermarkText.set(watermarkText);
    }

    public boolean isDropShadowVisible() {
        return dropShadowVisible.get();
    }

    public BooleanProperty dropShadowVisibleProperty() {
        return dropShadowVisible;
    }

    public void setDropShadowVisible(boolean dropShadowVisible) {
        this.dropShadowVisible.set(dropShadowVisible);
    }

    public PixelBuffer getBuffer() {
        return buffer.get();
    }

    public ObjectProperty<PixelBuffer> bufferProperty() {
        return buffer;
    }

    public void setBuffer(PixelBuffer pixelBuffer) {
        this.buffer.set(pixelBuffer);
    }

    public PixelShape getPixelShape() {
        return pixelShape.get();
    }

    public ObjectProperty<PixelShape> pixelShapeProperty() {
        return pixelShape;
    }

    public void setPixelShape(PixelShape pixelShape) {
        this.pixelShape.set(pixelShape);
    }

    public double getMargin() {
        return margin.get();
    }

    public DoubleProperty marginProperty() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin.set(margin);
    }

    public float getContrast() {
        return contrast.get();
    }

    public FloatProperty contrastProperty() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast.set(contrast);
    }

    public double getSpacing() {
        return spacing.get();
    }

    public DoubleProperty spacingProperty() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing.set(spacing);
    }

    public double getPixelSize() {
        return pixelSize.get();
    }

    public DoubleProperty pixelSizeProperty() {
        return pixelSize;
    }

    public void setPixelSize(double pixelSize) {
        this.pixelSize.set(pixelSize);
    }

    public int getDisplayWidth() {
        return displayWidth.get();
    }

    public ReadOnlyIntegerProperty displayWidthProperty() {
        return displayWidth.getReadOnlyProperty();
    }

    public int getDisplayHeight() {
        return displayHeight.get();
    }

    public ReadOnlyIntegerProperty displayHeightProperty() {
        return displayHeight.getReadOnlyProperty();
    }

    public Color getActivePixelColor() {
        return activePixelColor.get();
    }

    public ObjectProperty<Color> activePixelColorProperty() {
        return activePixelColor;
    }

    public void setActivePixelColor(Color activePixelColor) {
        this.activePixelColor.set(activePixelColor);
    }

    public Color getInactivePixelColor() {
        return inactivePixelColor.get();
    }

    public ObjectProperty<Color> inactivePixelColorProperty() {
        return inactivePixelColor;
    }

    public void setInactivePixelColor(Color inactivePixelColor) {
        this.inactivePixelColor.set(inactivePixelColor);
    }

    public Color getBacklightColor() {
        return backlightColor.get();
    }

    public ObjectProperty<Color> backlightColorProperty() {
        return backlightColor;
    }

    public void setBacklightColor(Color backlightColor) {
        this.backlightColor.set(backlightColor);
    }

    public boolean isEditable() {
        return editable.get();
    }

    public BooleanProperty editableProperty() {
        return editable;
    }

    //TODO: Implement editor feature
    public void setEditable(boolean editable) {
        this.editable.set(editable);
    }
    //</editor-fold>

    //<editor-fold desc="Overriden Methods">
    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
    }
    //</editor-fold>

    public WritableImage screenshot(WritableImage image) {
        boolean prev = dropShadowVisible.get();
        try {
            showWatermark.set(true);
            setDropShadowVisible(false);
            draw(); //re-draw to make sure that the screen dimensions are up to date
            return super.snapshot(null, image);
        } finally {
            setDropShadowVisible(prev);
        }
    }

    /**
     * Method to re-draw the canvas. This is a no-op if the internal animation timer is already running.
     */
    public void refresh() {
        if (!renderer.isRunning())
            draw();
    }

    /**
     * Reads the internal graphics buffer and draws it to the dot-matrix display screen
     */
    private void draw() {
        if (buffer.get() == null)
            return;

        GraphicsContext gc = getGraphicsContext2D();

        double lcdWidth = displayWidth.get();
        double lcdHeight = displayHeight.get();
        double pixelSize = this.pixelSize.get();
        double pixelWidth = pixelSize;
        double pixelHeight = pixelSize;
        double spacing = this.spacing.get();
        double margin = this.margin.get();
        PixelShape pixelShape = this.pixelShape.get();

        //Clear the canvas
        gc.clearRect(0, 0, getWidth(), getHeight());

        //Set backlight color
        if (isGradientBacklight()) {
            gc.setFill(createGradientBacklightColor());
        } else {
            gc.setFill(getBacklightColor());
        }
        gc.fillRect(0, 0, getWidth(), getHeight());

        //Set inactive pixel color
        gc.setFill(computeInactivePixelColor());

        //note x and y represents the actual pixel coordinates of the canvas
        //while pixelX and pixelY represents the coordinates of the GLCD
        double x = spacing + margin, y = spacing + margin;
        for (int pixelY = 0; pixelY < lcdHeight; pixelY++) {

            //process one column at a time
            for (int pixelX = 0; pixelX < lcdWidth; pixelX++) {
                int pixelState = buffer.get().read(pixelX, pixelY);
                if (pixelState == -1)
                    pixelState = 0;
                drawPixel(x, y, pixelState);
                x += pixelSize + spacing;
            }

            //reset x including the space and margin properties
            x = spacing + margin;
            y += pixelSize + spacing;
        }

        if (showWatermark.compareAndSet(true, false) && !StringUtils.isBlank(watermarkText.get())) {
            gc.setLineWidth(1.0);
            gc.setEffect(displayDropShadow);
            gc.setFill(watermarkColor.get());
            gc.setFont(watermarkFont.get());
            double strWidth = NodeUtil.computeStringWidth(watermarkText.get(), watermarkFont.get());
            double strHeight = NodeUtil.computeStringHeight(watermarkText.get(), watermarkFont.get());
            gc.fillText(watermarkText.get(), getWidth() - (strWidth + margin), (getHeight() - strHeight) + (margin / 2.0d));
            gc.setFill(null);
            gc.setEffect(null);
            gc.setFont(null);
        }
    }

    private Paint createGradientBacklightColor() {
        return new LinearGradient(0, 0, 0,
                1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, this.backlightColor.get()),
                new Stop(1.0, this.backlightColor.get().darker().darker())
        );
    }


    /**
     * Draw a pixel on the canvas
     *
     * @param x
     *         The X-coordinate of the canvas.
     * @param y
     *         The Y-coordinate of the canvas.
     * @param state
     *         1 = on, 0 = off
     */
    private void drawPixel(double x, double y, int state) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(state > 0 ? activePixelColor.get() : computeInactivePixelColor());
        if (pixelShape.get().equals(PixelShape.CIRCLE)) {
            gc.fillOval(x, y, pixelSize.get(), pixelSize.get());
        } else {
            gc.fillRect(x, y, pixelSize.get(), pixelSize.get());
        }
    }

    private Color changeColorOpacity(Property<Color> colorProperty, double opacity) {
        Color baseColor = colorProperty.getValue();
        return Color.color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), opacity);
    }

    private Color computeInactivePixelColor() {
        Color baseInactivePixelColor = inactivePixelColor.get();
        double opacity = contrast.get() / 100.0f;
        return Color.color(baseInactivePixelColor.getRed(), baseInactivePixelColor.getGreen(), baseInactivePixelColor.getBlue(), opacity);
    }

    /**
     * <p>Creates a {@link NumberBinding} binding to calculate the display's width and height based on the pixel size, spacing and margin properties.</p>
     *
     * <pre>
     *      Width calculation: (((displayWidth x pixelSize) + (displayWidth + spacing)) + spacing) + (margin x 2)
     *      Height calculation: (((displayHeight x pixelSize) + (displayHeight + spacing)) + spacing) + (margin x 2)
     *  </pre>
     *
     * @param property
     *         The property to be updated (e.g. width or height)
     *
     * @return The {@link NumberBinding} instance
     */
    private NumberBinding createCanvasResizeBinding(ReadOnlyIntegerProperty property) {
        return Bindings.add(Bindings.multiply(property, pixelSize), Bindings.multiply(property, spacing)).add(spacing).add(Bindings.multiply(margin, 2));
    }

    private Color updateOpacity(Color color, float value) {
        return Color.color(color.getRed(), color.getGreen(), color.getBlue(), value);
    }
}
