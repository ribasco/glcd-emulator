/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: StatusIndicator.java
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
package com.ibasco.glcdemu.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.shape.Circle;

public class StatusIndicator extends Circle {

    private static final PseudoClass ON = PseudoClass.getPseudoClass("on");

    private static final PseudoClass OFF = PseudoClass.getPseudoClass("off");

    private BooleanProperty activated = new SimpleBooleanProperty(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(ON, get());
            pseudoClassStateChanged(OFF, !get());
        }

        @Override
        public Object getBean() {
            return StatusIndicator.this;
        }
    };

    public StatusIndicator() {
        /*activated.addListener(observable -> {
            if (isActivated()) {
                pseudoClassStateChanged(ON, true);
                pseudoClassStateChanged(OFF, false);
            } else {
                pseudoClassStateChanged(OFF, true);
                pseudoClassStateChanged(ON, false);
            }
        });*/
        getStyleClass().add("status-indicator");
        setRadius(7.0f);
        off();
    }

    public void on() {
        if (!activated.isBound())
            activated.set(true);
    }

    public void off() {
        if (!activated.isBound())
            activated.set(false);
    }

    public void toggle() {
        if (!activated.isBound())
            activated.set(!activated.get());
    }

    public boolean isActivated() {
        return activated.get();
    }

    public BooleanProperty activatedProperty() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated.set(activated);
    }
}
