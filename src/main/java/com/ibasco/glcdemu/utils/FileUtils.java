package com.ibasco.glcdemu.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File system utility methods
 *
 * @author Rafael Ibasco
 */
public class FileUtils {
    /**
     * Creates a new directory if the path specified does not yet exist on the file system
     *
     * @param dirPath The directory path
     */
    public static void ensureDirectoryExistence(String dirPath) {
        if (StringUtils.isBlank(dirPath))
            return;
        Path pDirPath = Paths.get(dirPath);
        if (!Files.exists(pDirPath)) {
            try {
                Path pNewDir = Files.createDirectory(pDirPath);
                if (!Files.isDirectory(pNewDir)) {
                    throw new IOException("Not a valid directory '" + pNewDir.toString() + "'");
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create directory : " + pDirPath.toString(), e);
            }
        }
    }
}
