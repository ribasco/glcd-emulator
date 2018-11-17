/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: StageHelper.java
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
package com.ibasco.glcdemulator;

import com.ibasco.glcdemulator.exceptions.StageHelperException;
import com.ibasco.glcdemulator.utils.ResourceUtil;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class StageHelper {

    public static Stage createDialog(Window owner, String title, String viewResourceName) {
        return createDialog(owner, title, viewResourceName, null, null);
    }

    public static Stage createDialog(Window owner, String title, String viewResourceName, Modality modality) {
        return createDialog(owner, title, viewResourceName, null, null, null, modality);
    }

    public static Stage createDialog(Window owner, String title, String viewResourceName, Controller controller) {
        return createDialog(owner, title, viewResourceName, controller, null);
    }

    public static Stage createDialog(Window owner, String title, String viewResourceName, Controller controller, Scene scene) {
        return createDialog(owner, title, viewResourceName, controller, scene, null);
    }

    public static <T extends Parent> Stage createDialog(Window owner, String title, String viewResourceName, Controller controller, Scene scene, Consumer<T> func) {
        return createDialog(owner, title, viewResourceName, controller, scene, func, Modality.APPLICATION_MODAL);
    }

    public static <T extends Parent> Stage createDialog(Window owner, String title, String viewResourceName, Controller controller, Scene scene, Consumer<T> func, Modality modality) {
        return createStageFromResource(owner, true, modality, title, viewResourceName, controller, scene, func);
    }

    public static <T extends Parent> Stage createStageFromResource(Window owner, boolean resizable, Modality modality, String title, String viewResourceName, Controller controller, Scene scene, Consumer<T> func) {
        Stage stage;
        try {
            stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(modality);
            stage.setResizable(resizable);
            stage.setTitle(title);
            T content;
            if (controller == null) {
                content = ResourceUtil.loadFxmlResource(viewResourceName);
            } else {
                content = ResourceUtil.loadFxmlResource(viewResourceName, controller);
            }
            if (content == null)
                throw new IOException("Could not load view resource '" + viewResourceName + "'");
            if (func != null)
                func.accept(content);
            if (scene == null)
                scene = new Scene(content);
            Context.getInstance().getThemeManager().applyTheme(scene);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new StageHelperException(e);
        }
        return stage;
    }
}
