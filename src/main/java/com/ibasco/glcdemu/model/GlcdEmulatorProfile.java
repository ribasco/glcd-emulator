package com.ibasco.glcdemu.model;

import com.ibasco.glcdemu.annotations.Auditable;
import com.ibasco.glcdemu.annotations.Exclude;
import com.ibasco.glcdemu.enums.PixelShape;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class GlcdEmulatorProfile extends GlcdConfig {

    private IntegerProperty id = new SimpleIntegerProperty(1);

    private StringProperty name = new SimpleStringProperty("default");

    private StringProperty description = new SimpleStringProperty("Default Profile");

    private ObjectProperty<Color> lcdBacklightColor = new SimpleObjectProperty<>(Color.web("#242fed", 1.0));

    private ObjectProperty<Color> lcdActivePixelColor = new SimpleObjectProperty<>(Color.web("#FFFFFF", 1.0));

    private ObjectProperty<Color> lcdInactivePixelColor = new SimpleObjectProperty<>(Color.web("#3730f6", 0.8));

    private ObjectProperty<Integer> displaySizeWidth = new SimpleObjectProperty<>(128);

    private ObjectProperty<Integer> displaySizeHeight = new SimpleObjectProperty<>(64);

    private DoubleProperty lcdPixelSize = new SimpleDoubleProperty(5.0);

    private FloatProperty lcdContrast = new SimpleFloatProperty(0.5f);

    private DoubleProperty lcdSpacing = new SimpleDoubleProperty(5.0d);

    private DoubleProperty lcdMargin = new SimpleDoubleProperty(20.0d);

    private ObjectProperty<PixelShape> lcdPixelShape = new SimpleObjectProperty<>(PixelShape.RECTANGLE);

    public GlcdEmulatorProfile() {
        this("default");
    }

    public GlcdEmulatorProfile(String name) {
        setName(name);
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public GlcdEmulatorProfile(GlcdEmulatorProfile source) {
        this(source.getName());
        setId(-1);
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
        b.append(getId());
        b.append(getName());
        b.append(getDisplaySizeWidth());
        b.append(getDisplaySizeHeight());
        b.append(getLcdPixelSize());
        return b.toString();
    }
}
