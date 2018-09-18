package com.ibasco.glcdemu.controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.shape.Circle;

public class StatusIndicator extends Circle {

    private static PseudoClass ON_PC = PseudoClass.getPseudoClass("on");

    private static PseudoClass OFF_PC = PseudoClass.getPseudoClass("off");

    private BooleanProperty activated = new SimpleBooleanProperty(false);

    public StatusIndicator() {
        activated.addListener(observable -> {
            if (isActivated()) {
                pseudoClassStateChanged(ON_PC, true);
                pseudoClassStateChanged(OFF_PC, false);
            } else {
                pseudoClassStateChanged(OFF_PC, true);
                pseudoClassStateChanged(ON_PC, false);
            }
        });
        getStyleClass().add("status-indicator");
        setRadius(5.0f);
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
