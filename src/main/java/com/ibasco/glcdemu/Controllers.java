package com.ibasco.glcdemu;

import com.ibasco.glcdemu.controllers.GlcdEditProfileController;
import com.ibasco.glcdemu.controllers.GlcdEmulatorController;
import com.ibasco.glcdemu.controllers.GlcdFontBrowserController;
import com.ibasco.glcdemu.model.FontCacheDetails;
import com.ibasco.glcdemu.services.FontCacheService;

public class Controllers {

    private static GlcdFontBrowserController fontBrowserController;

    private static GlcdEditProfileController editProfileController;

    private static GlcdEmulatorController emulatorController;

    public static GlcdEmulatorController getEmulatorController() {
        if (emulatorController == null) {
            emulatorController = new GlcdEmulatorController();
        }
        return emulatorController;
    }

    static void setEmulatorController(GlcdEmulatorController controller) {
        emulatorController = controller;
    }

    public static GlcdEditProfileController getEditProfileController() {
        if (editProfileController == null) {
            editProfileController = new GlcdEditProfileController();
        }
        return editProfileController;
    }

    public static GlcdFontBrowserController getFontBrowserController() {
        if (fontBrowserController == null) {
            fontBrowserController = new GlcdFontBrowserController(new FontCacheService(), new FontCacheDetails());
        }
        return fontBrowserController;
    }
}
