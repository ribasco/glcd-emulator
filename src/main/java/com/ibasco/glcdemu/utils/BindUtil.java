/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: BindUtil.java
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
package com.ibasco.glcdemu.utils;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class BindUtil {
    public static <T> void bindToggleGroupToProperty(final ToggleGroup toggleGroup, final ObjectProperty<T> property) {
        // Check all toggles for required user data
        toggleGroup.getToggles().forEach(toggle -> {
            if (toggle.getUserData() == null) {
                throw new IllegalArgumentException("The ToggleGroup contains at least one Toggle without user data!");
            }
        });
        // Select initial toggle for current property state
        for (Toggle toggle : toggleGroup.getToggles()) {
            if (property.getValue() != null && property.getValue().equals(toggle.getUserData())) {
                toggleGroup.selectToggle(toggle);
                break;
            }
        }
        // Update property value on toggle selection changes
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            property.setValue((T) newValue.getUserData());
        });
    }

}
