package com.ibasco.glcdemu.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

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
        File fPath = new File(dirPath);
        if (!fPath.exists()) {
            if (!fPath.mkdir() && !fPath.isDirectory())
                throw new RuntimeException(new IOException("Could not create directory"));
        }
    }
}
