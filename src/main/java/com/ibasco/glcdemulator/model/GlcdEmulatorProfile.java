/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdEmulatorProfile.java
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
package com.ibasco.glcdemulator.model;

import com.ibasco.glcdemulator.annotations.Auditable;
import com.ibasco.glcdemulator.enums.PixelShape;
import com.ibasco.ucgdisplay.drivers.glcd.Glcd;
import com.ibasco.ucgdisplay.drivers.glcd.GlcdDisplay;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class GlcdEmulatorProfile extends GlcdConfig {

    private IntegerProperty id = new SimpleIntegerProperty(1);

    private StringProperty name = new SimpleStringProperty("default");

    private StringProperty description = new SimpleStringProperty("Default Profile");

    private ObjectProperty<Color> lcdBacklightColor = new SimpleObjectProperty<>(Color.web("#212121", 1.0));

    private ObjectProperty<Color> lcdActivePixelColor = new SimpleObjectProperty<>(Color.web("#fafafa", 1.0));

    private ObjectProperty<Color> lcdInactivePixelColor = new SimpleObjectProperty<>(Color.web("#fafafa", 0.0598f));

    private ObjectProperty<Integer> displaySizeWidth = new SimpleObjectProperty<>(128);

    private ObjectProperty<Integer> displaySizeHeight = new SimpleObjectProperty<>(64);

    private DoubleProperty lcdPixelSize = new SimpleDoubleProperty(5.0d);

    private FloatProperty lcdContrast = new SimpleFloatProperty(5.987805f);

    private DoubleProperty lcdSpacing = new SimpleDoubleProperty(1.4292f);

    private DoubleProperty lcdMargin = new SimpleDoubleProperty(9.63414f);

    private ObjectProperty<GlcdDisplay> display = new SimpleObjectProperty<>(Glcd.ST7920.D_128x64);

    private ObjectProperty<GlcdBusInterface> busInterface = new SimpleObjectProperty<>(GlcdBusInterface.PARALLEL_8080);

    private ObjectProperty<PixelShape> lcdPixelShape = new SimpleObjectProperty<>(PixelShape.RECTANGLE);

    public GlcdEmulatorProfile() {
        this("default");
    }

    public GlcdEmulatorProfile(String name) {
        setName(name);
        setId(-1);
    }

    public GlcdEmulatorProfile(GlcdEmulatorProfile source) {
        this(source.getName());
        setFile(null);
        setName(source.getName());
        setDescription(source.getDescription());
        setLcdBacklightColor(source.lcdBacklightColor.get());
        setLcdActivePixelColor(source.lcdActivePixelColor.get());
        setLcdInactivePixelColor(source.lcdInactivePixelColor.get());
        setLcdContrast(source.lcdContrast.get());
        setLcdPixelSize(source.lcdPixelSize.get());
        setDisplaySizeWidth(source.displaySizeWidth.get());
        setDisplaySizeHeight(source.displaySizeHeight.get());
        setLcdSpacing(source.lcdSpacing.get());
        setLcdMargin(source.lcdMargin.get());
        setLcdPixelShape(source.lcdPixelShape.get());
        setDisplay(source.getDisplay());
        setBusInterface(source.getBusInterface());
    }

    public GlcdBusInterface getBusInterface() {
        return busInterface.get();
    }

    public ObjectProperty<GlcdBusInterface> busInterfaceProperty() {
        return busInterface;
    }

    public void setBusInterface(GlcdBusInterface busInterface) {
        this.busInterface.set(busInterface);
    }

    public GlcdDisplay getDisplay() {
        return display.get();
    }

    public ObjectProperty<GlcdDisplay> displayProperty() {
        return display;
    }

    public void setDisplay(GlcdDisplay display) {
        this.display.set(display);
    }

    public PixelShape getLcdPixelShape() {
        return lcdPixelShape.get();
    }

    public ObjectProperty<PixelShape> lcdPixelShapeProperty() {
        return lcdPixelShape;
    }

    public void setLcdPixelShape(PixelShape lcdPixelShape) {
        this.lcdPixelShape.set(lcdPixelShape);
    }

    public double getLcdMargin() {
        return lcdMargin.get();
    }

    public DoubleProperty lcdMarginProperty() {
        return lcdMargin;
    }

    public void setLcdMargin(double lcdMargin) {
        this.lcdMargin.set(lcdMargin);
    }

    public Color getLcdBacklightColor() {
        return lcdBacklightColor.get();
    }

    public ObjectProperty<Color> lcdBacklightColorProperty() {
        return lcdBacklightColor;
    }

    public void setLcdBacklightColor(Color lcdBacklightColor) {
        this.lcdBacklightColor.set(lcdBacklightColor);
    }

    public Color getLcdActivePixelColor() {
        return lcdActivePixelColor.get();
    }

    public ObjectProperty<Color> lcdActivePixelColorProperty() {
        return lcdActivePixelColor;
    }

    public void setLcdActivePixelColor(Color lcdActivePixelColor) {
        this.lcdActivePixelColor.set(lcdActivePixelColor);
    }

    public Integer getDisplaySizeWidth() {
        return displaySizeWidth.get();
    }

    public ObjectProperty<Integer> displaySizeWidthProperty() {
        return displaySizeWidth;
    }

    public void setDisplaySizeWidth(Integer displaySizeWidth) {
        this.displaySizeWidth.set(displaySizeWidth);
    }

    public Integer getDisplaySizeHeight() {
        return displaySizeHeight.get();
    }

    public ObjectProperty<Integer> displaySizeHeightProperty() {
        return displaySizeHeight;
    }

    public void setDisplaySizeHeight(Integer displaySizeHeight) {
        this.displaySizeHeight.set(displaySizeHeight);
    }

    public double getLcdPixelSize() {
        return lcdPixelSize.get();
    }

    public DoubleProperty lcdPixelSizeProperty() {
        return lcdPixelSize;
    }

    public void setLcdPixelSize(double lcdPixelSize) {
        this.lcdPixelSize.set(lcdPixelSize);
    }

    public void incrementPixel(double steps) {
        this.lcdPixelSize.set(this.lcdPixelSize.get() + steps);
    }

    public void decrementPixel(double steps) {
        this.lcdPixelSize.set(this.lcdPixelSize.get() - steps);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Auditable(enabled = false)
    public boolean isNew() {
        return getFile() == null;
    }

    public Color getLcdInactivePixelColor() {
        return lcdInactivePixelColor.get();
    }

    public ObjectProperty<Color> lcdInactivePixelColorProperty() {
        return lcdInactivePixelColor;
    }

    public void setLcdInactivePixelColor(Color lcdInactivePixelColor) {
        this.lcdInactivePixelColor.set(lcdInactivePixelColor);
    }

    public float getLcdContrast() {
        return lcdContrast.get();
    }

    public FloatProperty lcdContrastProperty() {
        return lcdContrast;
    }

    public void setLcdContrast(float lcdContrast) {
        this.lcdContrast.set(lcdContrast);
    }

    public double getLcdSpacing() {
        return lcdSpacing.get();
    }

    public DoubleProperty lcdSpacingProperty() {
        return lcdSpacing;
    }

    public void setLcdSpacing(double lcdSpacing) {
        this.lcdSpacing.set(lcdSpacing);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlcdEmulatorProfile that = (GlcdEmulatorProfile) o;
        return Objects.equals(id.get(), that.id.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    @Override
    public String toString() {
        ToStringBuilder b = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
        b.append("Profile = {");
        b.append(getId());
        b.append(getName());
        b.append(getDisplaySizeWidth());
        b.append(getDisplaySizeHeight());
        b.append(getLcdPixelSize());
        b.append("}");
        return b.toString();
    }
}
