package com.ibasco.glcdemu.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class GlcdLog {

    private ObjectProperty<LocalDateTime> timestamp = new SimpleObjectProperty<>();

    private StringProperty type = new SimpleStringProperty();

    private StringProperty data = new SimpleStringProperty();

    public GlcdLog(String type, String data) {
        this(LocalDateTime.now(), type, data);
    }

    public GlcdLog(LocalDateTime timestamp, String type, String data) {
        setTimestamp(timestamp);
        setType(type);
        setData(data);
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getData() {
        return data.get();
    }

    public StringProperty dataProperty() {
        return data;
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public LocalDateTime getTimestamp() {
        return timestamp.get();
    }

    public ObjectProperty<LocalDateTime> timestampProperty() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp.set(timestamp);
    }
}
