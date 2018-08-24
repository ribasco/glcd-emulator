package com.ibasco.glcdemu;

import javafx.stage.Stage;

abstract public class GlcdController {
    protected final Stage stage;

    public GlcdController(Stage stage) {
        this.stage = stage;
    }

    /**
     * This is called once the stage is made available for the {@link GlcdController}
     */
    abstract public void onInit();

    abstract public boolean onClose();

    public Context getContext() {
        return Context.getInstance();
    }

    public Stage getStage() {
        return stage;
    }
}
