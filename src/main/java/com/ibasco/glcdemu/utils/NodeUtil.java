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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Helper class for misc {@link Node} operations
 *
 * @author Rafael Ibasco
 */
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
    public static void saveNodeImageToFile(final Node node, File imageFile, final double width, final double height) throws IOException {
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
            AtomicReference<IOException> err = new AtomicReference<>();

            Runnable r = () -> {
                try {
                    WritableImage wim = new WritableImage((int) width, (int) height);
                    node.snapshot(null, wim);
                    ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", finalImageFile);
                } catch (IOException e) {
                    log.error("Error encountered during screen capture", e);
                    err.set(e);
                } finally {
                    latch.countDown();
                }
            };

            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(r);
                latch.await(); //block until the snapshot operation is completed
            } else {
                r.run();
            }

            if (err.get() != null)
                throw err.get();
        } catch (InterruptedException e) {
            log.warn("Save node interrupted", e);
        }
    }
}
