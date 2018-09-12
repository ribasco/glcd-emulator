package com.ibasco.glcdemu;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

abstract public class GlcdController implements Initializable {

    private boolean initialized = false;

    public GlcdController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (initialized)
            return;
        initialized = true;
        initializeOnce();
    }

    protected void initializeOnce() {
    }

    public Context getContext() {
        return Context.getInstance();
    }
}
