/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: FileUtils.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 - 2019 Rafael Luis Ibasco
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

import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.constants.Common;
import com.ibasco.glcdemulator.exceptions.FileUtilsException;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
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
     * @param dirPath
     *         The directory path
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
                throw new FileUtilsException("Unable to create directory : " + pDirPath.toString(), e);
            }
        }
    }

    public static File openFileFromDialog(String title, String initDirectory, FileChooser.ExtensionFilter extFilters, Window owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (!StringUtils.isBlank(initDirectory)) {
            fileChooser.setInitialDirectory(new File(initDirectory));
        } else {
            fileChooser.setInitialDirectory(new File(Common.USER_DIR));
        }
        fileChooser.getExtensionFilters().add(extFilters);
        if (owner == null)
            owner = Context.getPrimaryStage();
        return fileChooser.showOpenDialog(owner);
    }
}
