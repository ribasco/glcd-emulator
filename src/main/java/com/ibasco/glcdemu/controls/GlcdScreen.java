package com.ibasco.glcdemu.controls;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class GlcdScreen extends Canvas {

    private static final Logger log = LoggerFactory.getLogger(GlcdScreen.class);

    private DoubleProperty pixelSize = new SimpleDoubleProperty(5.0);

    private IntegerProperty displayWidth = new SimpleIntegerProperty(128);

    private IntegerProperty displayHeight = new SimpleIntegerProperty(64);

    private SimpleObjectProperty<Color> foregroundColor = new SimpleObjectProperty<>(Color.WHITE);

    private ObjectProperty<Color> backgroundColor = new SimpleObjectProperty<>(Color.DODGERBLUE);

    private short[][] buffer;

    private byte[][] bufferext = new byte[8][16];

    private AnimationTimer animTimer;

    public GlcdScreen() {
        setStyle("-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        buffer = new short[32][16];

        drawGrid();
        animTimer = new AnimationTimer() {
            private long prevMillis = 0;

            @Override
            public void handle(long now) {
            }
        };
        animTimer.start();
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

    @Override
    public boolean isResizable() {
        return false;
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

    public Color getForegroundColor() {
        return foregroundColor.get();
    }

    public ObjectProperty<Color> foregroundColorProperty() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor.set(foregroundColor);
    }

    public Color getBackgroundColor() {
        return backgroundColor.get();
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor.set(backgroundColor);
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
    }

    public void setCursor() {

    }

    public void setPixel() {

    }

    public void refresh() {
        drawGrid();
        drawPixels();
    }

    private void drawGrid() {
        GraphicsContext gc = getGraphicsContext2D();

        //Update Canvas Size
        setWidth(displayWidth.get() * pixelSize.get());
        setHeight(displayHeight.get() * pixelSize.get());

        //Set pixel border width
        gc.setLineWidth(0.1);

        for (int row = 0; row < displayHeight.get(); ++row) {
            for (int col = 0; col < displayWidth.get(); ++col) {
                int x = (int) (pixelSize.get() * col);
                int y = (int) (pixelSize.get() * row);
                gc.strokeRect(x, y, pixelSize.get(), pixelSize.get());
                gc.setFill(backgroundColor.get());
                gc.fillRect(x, y, pixelSize.get(), pixelSize.get());
            }
        }
    }

    private void drawPixels() {
        GraphicsContext gc = getGraphicsContext2D();
        int pSize = (int) pixelSize.get();
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

                    gc.setFill(bit > 0 ? foregroundColor.get() : backgroundColor.get());
                    gc.fillRect(pX, pY, pSize - 1, pSize - 1);
                    pixelIndex++;
                }
            }
        }
    }
}
