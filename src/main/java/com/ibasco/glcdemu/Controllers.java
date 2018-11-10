/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: Controllers.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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
package com.ibasco.glcdemu;

import com.ibasco.glcdemu.controllers.GlcdAboutController;
import com.ibasco.glcdemu.controllers.GlcdEditProfileController;
import com.ibasco.glcdemu.controllers.GlcdEmulatorController;
import com.ibasco.glcdemu.controllers.GlcdFontBrowserController;
import com.ibasco.glcdemu.model.FontCacheDetails;
import com.ibasco.glcdemu.services.FontCacheService;

public class Controllers {

    private static GlcdFontBrowserController fontBrowserController;

    private static GlcdEditProfileController editProfileController;

    private static GlcdEmulatorController emulatorController;

    private static GlcdAboutController aboutController;

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

    public static GlcdAboutController getAboutController() {
        if (aboutController == null) {
            aboutController = new GlcdAboutController();
        }
        return aboutController;
    }
}
