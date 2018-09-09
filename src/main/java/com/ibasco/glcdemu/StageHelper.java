package com.ibasco.glcdemu;

import com.ibasco.glcdemu.utils.ResourceUtil;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.function.Consumer;

public class StageHelper {

    public static Stage createDialog(Window owner, String title, String viewResourceName, GlcdController controller) {
        return createDialog(owner, title, viewResourceName, controller, null);
    }

    public static Stage createDialog(Window owner, String title, String viewResourceName, GlcdController controller, Scene scene) {
        return createDialog(owner, title, viewResourceName, controller, scene, null);
    }

    public static <T extends Parent> Stage createDialog(Window owner, String title, String viewResourceName, GlcdController controller, Scene scene, Consumer<T> func) {
        return createStageFromResource(owner, true, Modality.APPLICATION_MODAL, title, viewResourceName, controller, scene, func);
    }

    public static <T extends Parent> Stage createStageFromResource(Window owner, boolean resizable, Modality modality, String title, String viewResourceName, GlcdController controller, Scene scene, Consumer<T> func) {
        Stage stage;
        try {
            stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(modality);
            stage.setResizable(resizable);
            stage.setTitle(title);
            T content = ResourceUtil.loadFxmlResource(viewResourceName, controller);
            if (content == null)
                throw new IOException("Could not load view resource '" + viewResourceName + "'");
            if (func != null)
                func.accept(content);
            if (scene == null)
                scene = new Scene(content);
            Context.getInstance().getThemeManager().applyTheme(scene);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stage;
    }
}
