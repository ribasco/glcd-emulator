/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: NodeUtil.java
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
package com.ibasco.glcdemulator.utils;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    public static String toHexString(Color color) throws NullPointerException {
        return String.format("#%02x%02x%02x",
                (int) (255 * color.getRed()),
                (int) (255 * color.getGreen()),
                (int) (255 * color.getBlue()));
    }

    public static double computeStringWidth(String text, Font font) {
        final Text tmp = new Text(text);
        tmp.setFont(font);
        return tmp.getLayoutBounds().getWidth();
    }

    public static double computeStringHeight(String text, Font font) {
        final Text tmp = new Text(text);
        tmp.setFont(font);
        return tmp.getLayoutBounds().getHeight();
    }
}
