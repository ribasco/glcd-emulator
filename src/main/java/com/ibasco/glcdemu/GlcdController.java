package com.ibasco.glcdemu;

import com.ibasco.glcdemu.beans.GlcdConfigEmulator;
import com.ibasco.glcdemu.beans.GlcdConfigEmulatorProfile;
import com.ibasco.glcdemu.services.GlcdConfigProfileService;
import javafx.fxml.Initializable;

abstract public class GlcdController implements Initializable {
    abstract public void onInit();
    abstract public void onClose();

    protected GlcdConfigProfileService profileManager;

    protected GlcdConfigEmulatorProfile profileConfig;

    public GlcdConfigEmulator getProfileConfig() {
        return profileConfig;
    }
}
