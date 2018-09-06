package com.ibasco.glcdemu.utils;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class NodeUtil {

    private static final Logger log = LoggerFactory.getLogger(NodeUtil.class);

    /**
     * Captures a snapshot image of a node and saves it to the filesystem as PNG format
     *
     * @param node
     *         The target node
     * @param imageFile
     *         The destination {@link File}
     */
    public static void saveNodeImageToFile(Node node, File imageFile, double width, double height) {
        if (imageFile == null) {
            log.warn("Image file is null");
            return;
        }

        if (!imageFile.getName().endsWith(".png")) {
            imageFile = new File(imageFile.getAbsolutePath() + ".png");
        }

        try {
            CountDownLatch latch = new CountDownLatch(1);
            File finalImageFile = imageFile;
            Platform.runLater(() -> {
                try {
                    WritableImage wim = new WritableImage((int) width, (int) height);
                    node.snapshot(null, wim);
                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", finalImageFile);
                } catch (IOException e) {
                    log.error("Error encoutnered during screen capture", e);
                } finally {
                    latch.countDown();
                }
            });
            latch.await();
        } catch (InterruptedException e) {
            log.warn("Interrupted", e);
        }
    }
}
