package com.ibasco.glcdemu.controls;

import com.ibasco.glcdemu.enums.PixelShape;
import javafx.animation.AnimationTimer;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"unused", "UnnecessaryLocalVariable"})
public class GlcdScreen extends Canvas {

    private static final Logger log = LoggerFactory.getLogger(GlcdScreen.class);

    //<editor-fold desc="Properties">
    private DoubleProperty pixelSize = new SimpleDoubleProperty(5.0);

    private IntegerProperty displayWidth = new SimpleIntegerProperty(128);

    private IntegerProperty displayHeight = new SimpleIntegerProperty(64);

    private ObjectProperty<Color> inactivePixelColor = new SimpleObjectProperty<>(Color.web("#000000", 0.5f));

    private ObjectProperty<Color> activePixelColor = new SimpleObjectProperty<>(Color.web("#000000", 1.0));

    private ObjectProperty<Color> backlightColor = new SimpleObjectProperty<>(Color.web("#BDD630", 1.0));

    private FloatProperty contrast = new SimpleFloatProperty(0.5f);

    private DoubleProperty spacing = new SimpleDoubleProperty(1.0d);

    private DoubleProperty margin = new SimpleDoubleProperty(20.0d);

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private BooleanProperty invalidatedDisplay = new SimpleBooleanProperty(false);

    private BooleanProperty editable = new SimpleBooleanProperty(false);

    private ObjectProperty<PixelShape> pixelShape = new SimpleObjectProperty<>(PixelShape.RECTANGLE);

    private NumberBinding widthBinding = createCanvasResizeBinding(displayWidth);

    private NumberBinding heightBinding = createCanvasResizeBinding(displayHeight);
    //</editor-fold>

    private final AtomicBoolean modifierKeyPressed = new AtomicBoolean(false);

    private byte[][] buffer;

    private AnimationTimer animTimer;

    public GlcdScreen() {
        setStyle("-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        contrast.addListener((observable, oldValue, newValue) -> {
            float value = (float) newValue / 100f;
            inactivePixelColor.set(updateOpacity(inactivePixelColor.get(), value));
        });

        //drawGrid();
        /*refreshOnChange(
                displayWidth,
                displayHeight,
                pixelSize,
                activePixelColor,
                inactivePixelColor,
                backlightColor,
                contrast,
                spacing,
                margin,
                pixelShape
        );*/

        ChangeListener<Number> sizeValidator = (observable, oldValue, newValue) -> {
            if (newValue != null && (((int) newValue % 8) != 0)) {
                SimpleIntegerProperty property = (SimpleIntegerProperty) observable;
                if (!property.isBound()) {
                    property.setValue(oldValue);
                }
                throw new RuntimeException("Illegal display width/height. Must be a multiple of 8 (" + observable.getClass().getSimpleName() + ")");
            }
        };

        displayWidthProperty().addListener(sizeValidator);
        displayHeightProperty().addListener(sizeValidator);

        animTimer = new AnimationTimer() {
            private long prevMillis = 0;

            @Override
            public void handle(long now) {
                drawGrid();
            }
        };
        animTimer.start();
    }

    //<editor-fold desc="Property Getter/Setters">
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

    private void refreshOnChange(Observable... observables) {
        for (Observable observable : observables) {
            observable.addListener(observable1 -> {
                refresh();
            });
        }
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

    public IntegerProperty displayWidthProperty() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth.set(displayWidth);
    }

    public int getDisplayHeight() {
        return displayHeight.get();
    }

    public IntegerProperty displayHeightProperty() {
        return displayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight.set(displayHeight);
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

    public void refresh() {
        drawGrid();
        //drawPixels();
    }

    /**
     * Reads the internal graphics buffer and draws it to the dot-matrix display screen
     */
    private void drawGrid() {
        if (!initialized.getAndSet(true)) {
            widthProperty().bind(widthBinding);
            heightProperty().bind(heightBinding);
        }

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
        LinearGradient backlightColor = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0.0, this.backlightColor.get()), new Stop(1.0, this.backlightColor.get().darker().darker()));
        gc.setFill(backlightColor);
        gc.fillRect(0, 0, getWidth(), getHeight());

        //Set inactive pixel color
        gc.setFill(computeInactivePixelColor());

        double x = spacing + margin, y = spacing + margin;
        for (int pixelY = 0; pixelY < lcdHeight; pixelY++) {
            for (int pixelX = 0; pixelX < lcdWidth; pixelX++) {
                drawPixel(x, y, getBitState(pixelX, pixelY));
                x += pixelSize + spacing;
            }
            x = spacing + margin;
            y += pixelSize + spacing;
        }
    }

    /**
     * Checks the bit state in the internal display buffer based on the specified x, y pixel coordinates.
     *
     * @param x The x-coordinate of the LCD
     * @param y The y-coordinate of the LCD
     * @return Returns <code>True</code> if the bit/pixel is set
     */
    private boolean getBitState(int x, int y) {
        GraphicsContext gc = getGraphicsContext2D();
        return RandomUtils.nextBoolean();
    }

    /**
     * Draw a pixel on the canvas
     *
     * @param x     The X-coordinate of the canvas.
     * @param y     The Y-coordinate of the canvas.
     * @param state <code>True</code> to turn on the pixel
     */
    private void drawPixel(double x, double y, boolean state) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(state ? activePixelColor.get() : computeInactivePixelColor());
        if (pixelShape.get().equals(PixelShape.CIRCLE)) {
            gc.fillOval(x, y, pixelSize.get(), pixelSize.get());
        } else {
            gc.fillRect(x, y, pixelSize.get(), pixelSize.get());
        }

    }

    private Color computeInactivePixelColor() {
        Color baseInactivePixelColor = inactivePixelColor.get();
        double opacity = contrast.get() / 100.0f;
        return Color.color(baseInactivePixelColor.getRed(), baseInactivePixelColor.getGreen(), baseInactivePixelColor.getBlue(), opacity);
    }

    private void drawPixels() {
        GraphicsContext gc = getGraphicsContext2D();
        int pSize = (int) pixelSize.get();
        int width = displayWidth.get();
        int height = displayHeight.get();

        for (int y = 0; y < 32; y++) {
            int pixelIndex = 0;

            for (int x = 0; x < 16; x++) {
                short data = buffer[y][x];

                //Start from MSB to LSB
                for (int bitIndex = 16; bitIndex > 0; bitIndex--) {
                    int bit = data & (1 << bitIndex);
                    int pxOffset = (x > 7) ? -128 : 0;
                    int pyOffset = (x > 7) ? 32 : 0;
                    int pX = (pixelIndex + pxOffset) * pSize;
                    int pY = (pyOffset + y) * pSize;

                    gc.setFill(bit > 0 ? activePixelColor.get() : inactivePixelColor.get());
                    gc.fillRect(pX, pY, pSize - 1, pSize - 1);
                    pixelIndex++;
                }
            }
        }
    }

    /**
     * <p>Creates a {@link NumberBinding} binding to calculate the display's width and height based on the pixel size, spacing and margin properties.</p>
     *
     * @param property The property to be updated (e.g. width or height)
     * @return The {@link NumberBinding} instance
     */
    private NumberBinding createCanvasResizeBinding(IntegerProperty property) {
        return Bindings.add(Bindings.multiply(property, pixelSize), Bindings.multiply(property, spacing)).add(spacing).add(Bindings.multiply(margin, 2));
    }

    private Color updateOpacity(Color color, float value) {
        return Color.color(color.getRed(), color.getGreen(), color.getBlue(), value);
    }
}
