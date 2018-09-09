package com.ibasco.glcdemu;

abstract public class GlcdController {
    //protected final Stage stage;

    public GlcdController() {
        //this.stage = stage;
    }

    /**
     * This is called once the stage has been made available for the {@link GlcdController}
     */
    public void onInit() {
    }

    public boolean onClose() {
        return true;
    }

    public Context getContext() {
        return Context.getInstance();
    }

    /*public Stage getStage() {
        return stage;
    }*/
}
