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
