package com.ibasco.glcdemu.utils;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class CanvasUtil {

    private static final Logger log = LoggerFactory.getLogger(CanvasUtil.class);

    public static void saveCanvasImage(Canvas canvas, File imageFile) {
        if (imageFile != null) {
            //make sure we include the extension
            if (!imageFile.getName().endsWith(".png")) {
                imageFile = new File(imageFile.getAbsolutePath() + ".png");
            }
            try {
                CountDownLatch latch = new CountDownLatch(1);
                File finalImageFile = imageFile;
                Platform.runLater(() -> {
                    try {
                        WritableImage wim = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                        canvas.snapshot(null, wim);
                        ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", finalImageFile);
                        latch.countDown();
                    } catch (IOException e) {
                        log.error("Error encoutnered during screen capture", e);
                    }
                });
                latch.await();
            } catch (InterruptedException e) {
                log.warn("Interrupted", e);
            }
        }
        //log.info("Saved file to : {}", imageFile.getAbsolutePath());
    }
}
