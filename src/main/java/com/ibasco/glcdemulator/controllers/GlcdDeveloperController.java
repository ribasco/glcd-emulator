/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: GlcdDeveloperController.java
 * 
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 - 2019 Rafael Luis Ibasco
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
package com.ibasco.glcdemulator.controllers;

import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.Controller;
import com.ibasco.glcdemulator.DriverFactory;
import com.ibasco.glcdemulator.Stages;
import com.ibasco.glcdemulator.controls.GlcdScreen;
import com.ibasco.glcdemulator.emulator.BufferLayout;
import com.ibasco.glcdemulator.emulator.BufferLayoutFactory;
import com.ibasco.glcdemulator.exceptions.ExportCSVException;
import com.ibasco.glcdemulator.utils.ByteUtils;
import com.ibasco.glcdemulator.utils.DialogUtil;
import com.ibasco.glcdemulator.utils.FileUtils;
import com.ibasco.glcdemulator.utils.PixelBuffer;
import com.ibasco.ucgdisplay.core.u8g2.U8g2Message;
import com.ibasco.ucgdisplay.drivers.glcd.*;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBufferType;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusType;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;
import com.jfoenix.controls.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlcdDeveloperController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(GlcdDeveloperController.class);
    private static final String DEFAULT_BASE64_VAL = "I2RlZmluZSByYXNwYmVycnlwaV9zbWFsbF93aWR0aCA5NQ0KI2RlZmluZSByYXNwYmVycnlwaV9zbWFsbF9oZWlnaHQgNzQNCnN0YXRpYyB1bnNpZ25lZCBjaGFyIHJhc3BiZXJyeXBpX3NtYWxsX2JpdHNbXSA9IHsNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHg3ZiwgMHgwMCwgMHg4MCwgMHgzZiwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4ZjgsIDB4ZmYsIDB4MDMsIDB4ZjAsIDB4ZmYsIDB4MDcsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDdlLCAweGFiLCAweDBmLCAweDdjLCAweGE1LCAweDFmLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwZiwgMHgwMCwgMHgxZSwgMHgxZSwgMHgwMCwgMHgzYywgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDMsIDB4MDAsIDB4MzgsIDB4MDcsIDB4MDAsIDB4MzAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDA3LCAweDAwLCAweDMwLCAweDAzLCAweDAwLCAweDMwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMywgMHgwMSwgMHhlMCwgMHgwMSwgMHgwMCwgMHgzMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDcsIDB4MDQsIDB4ZTAsIDB4MDEsIDB4MDgsIDB4MzAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAzLCAweDE4LCAweGMwLCAweDAxLCAweDA2LCAweDMwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwNiwgMHg2MCwgMHhjMCwgMHg4MSwgMHgwMSwgMHgxOCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MGUsIDB4ODAsIDB4YzEsIDB4YzEsIDB4MDAsIDB4MWMsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDBlLCAweDAwLCAweGUzLCAweDMxLCAweDAwLCAweDFjLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgxYywgMHgwMCwgMHhmNiwgMHgxYiwgMHgwMCwgMHgwZSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MTgsIDB4MDAsIDB4ZmMsIDB4MGYsIDB4MDAsIDB4MDYsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDMwLCAweDAwLCAweGZjLCAweDBmLCAweDAwLCAweDAzLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHg3MCwgMHgwMCwgMHhmYywgMHgxZiwgMHg4MCwgMHgwMywgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4YzAsIDB4MDAsIDB4ZmYsIDB4M2YsIDB4YzAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweGMwLCAweGM3LCAweGZmLCAweGZmLCAweGY4LCAweDAwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHhmZiwgMHgwZiwgMHhmYywgMHgzZiwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4YzAsIDB4ODcsIDB4MDMsIDB4ZjAsIDB4ZmMsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweGUwLCAweDgxLCAweDAxLCAweDYwLCAweGUwLCAweDAxLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHg3MCwgMHhjMCwgMHgwMCwgMHhjMCwgMHg4MCwgMHgwMywgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MzAsIDB4ZTAsIDB4MDEsIDB4YzAsIDB4MDEsIDB4MDMsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDM4LCAweGYwLCAweDAxLCAweGUwLCAweDAzLCAweDA3LCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgxOCwgMHhmOCwgMHgwMywgMHhmMCwgMHgwNywgMHgwNiwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MTgsIDB4ZmMsIDB4M2YsIDB4ZmUsIDB4MGYsIDB4MDYsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDFjLCAweDNmLCAweGZjLCAweDBmLCAweDNlLCAweDBlLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHg5YywgMHgwZiwgMHhmOCwgMHgwMywgMHg3OCwgMHgwZSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4ZmMsIDB4MDMsIDB4ZTAsIDB4MDMsIDB4ZjAsIDB4MGYsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweGZlLCAweDAzLCAweGUwLCAweDAxLCAweGUwLCAweDFmLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHhlZiwgMHgwMSwgMHhlMCwgMHgwMSwgMHhjMCwgMHgzOSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4ODAsIDB4ZTMsIDB4MDAsIDB4YzAsIDB4MDEsIDB4YzAsIDB4NzEsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDgwLCAweGUxLCAweDAwLCAweGMwLCAweDAxLCAweGMwLCAweDYxLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHhjMCwgMHhjMSwgMHgwMCwgMHhjMCwgMHgwMSwgMHg4MCwgMHhlMSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4YzAsIDB4ZTAsIDB4MDAsIDB4ZTAsIDB4MDEsIDB4ODAsIDB4YzEsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweGMwLCAweGUwLCAweDAwLCAweGUwLCAweDAxLCAweDgwLCAweGMxLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHhlMCwgMHhlMCwgMHgwMCwgMHhlMCwgMHgwMywgMHhjMCwgMHhjMSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4ZTAsIDB4ZTAsIDB4MDAsIDB4ZjAsIDB4MDcsIDB4YzAsIDB4YzEsIDB4MDEsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweGMwLCAweGUwLCAweDAxLCAweGY4LCAweDBmLCAweGUwLCAweGMxLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHhjMCwgMHhlMCwgMHgwMywgMHhmYywgMHgxZiwgMHhmMCwgMHhjMSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4YzAsIDB4ZjEsIDB4MGYsIDB4MDcsIDB4ZjgsIDB4ZmYsIDB4ZTMsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDgwLCAweGYxLCAweGZmLCAweDAzLCAweGYwLCAweGZmLCAweDYzLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHg4MCwgMHhmYiwgMHhmZiwgMHgwMSwgMHhlMCwgMHgzZiwgMHg3ZSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MWYsIDB4ZmYsIDB4MDAsIDB4YzAsIDB4MGYsIDB4M2MsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDBmLCAweGZjLCAweDAwLCAweGMwLCAweDA3LCAweDNjLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwZSwgMHhmOCwgMHgwMCwgMHg4MCwgMHgwMywgMHgxYywgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MGUsIDB4ZjAsIDB4MDAsIDB4ODAsIDB4MDEsIDB4MWMsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDBlLCAweGUwLCAweDAwLCAweGMwLCAweDAxLCAweDFjLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwYywgMHhjMCwgMHgwMCwgMHhjMCwgMHgwMCwgMHgwYywgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MGMsIDB4YzAsIDB4MDEsIDB4YzAsIDB4MDAsIDB4MGMsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDFjLCAweGMwLCAweDAxLCAweDYwLCAweDAwLCAweDBjLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgxOCwgMHg4MCwgMHgwNywgMHg3MCwgMHgwMCwgMHgwNiwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MzgsIDB4ODAsIDB4MGYsIDB4N2MsIDB4MDAsIDB4MDcsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDcwLCAweGMwLCAweGZmLCAweDdmLCAweDgwLCAweDAzLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHhlMCwgMHhjMSwgMHhmZiwgMHhmZiwgMHhlMCwgMHgwMSwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4YzAsIDB4ZmYsIDB4MDcsIDB4ZjgsIDB4ZmYsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweGZmLCAweDAxLCAweGMwLCAweDNmLCAweDAwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHhmYywgMHgwMCwgMHhjMCwgMHgwZiwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4ZjgsIDB4MDAsIDB4YzAsIDB4MDcsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweGUwLCAweDAxLCAweGUwLCAweDAxLCAweDAwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHg4MCwgMHgwNywgMHg3MCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MWYsIDB4M2UsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweGZjLCAweDBmLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHhmMCwgMHgwMywgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLA0KICAgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwgMHgwMCwNCiAgIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsIDB4MDAsDQogICAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwLCAweDAwIH07DQo=";
    private static final Set<Class<?>> primitiveNumbers = Stream.of(int.class, long.class, float.class, double.class, byte.class, short.class).collect(Collectors.toSet());

    //<editor-fold desc="UI Controls">
    @FXML
    private VBox vbRoot;

    @FXML
    private JFXComboBox<GlcdDisplayDetails> cbDisplay;

    @FXML
    private JFXListView<Method> cbDrawOperation;

    @FXML
    private GridPane gpParams;

    @FXML
    private JFXButton btnClearLogs;

    @FXML
    private JFXButton btnInvoke;

    @FXML
    private JFXCheckBox cbSendBuffer;

    @FXML
    private TableView<EventLogEntry> tvEventLog;

    @FXML
    private Label lblBytesReceived;

    @FXML
    private JFXButton btnExportCsv;

    @FXML
    private JFXCheckBox checkDefaultFont;

    @FXML
    private JFXComboBox<GlcdFont> cbDefaultFont;

    @FXML
    private JFXListView<U8g2Message> lvFilters;

    @FXML
    private JFXComboBox<U8g2Message> cbFilters;

    @FXML
    private JFXButton btnAddFilter;

    @FXML
    private ToggleGroup filterType;

    @FXML
    private JFXCheckBox checkAutoGen;

    @FXML
    private GlcdScreen screenOutput;

    @FXML
    private JFXCheckBox checkUseBufferStrategy;

    @FXML
    private JFXComboBox<GlcdBufferType> cbBufferStrategy;

    private JFXSnackbar snackbar;
    //</editor-fold>

    private final DateTimeFormatter csvFileNameFormatter = DateTimeFormatter.ofPattern("YYYYMMddkkmmss'_EventLog.csv'");

    private ObjectProperty<Method> selectedMethod = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdDisplayDetails> selectedDisplay = new SimpleObjectProperty<>();

    private MethodDetails currentMethod;

    private GlcdDriver virtualDriver;

    private AtomicInteger numOfBytes = new AtomicInteger();

    private AtomicInteger eventIndex = new AtomicInteger(0);

    private ObservableList<EventLogEntry> logEntries = FXCollections.observableArrayList();

    private FilteredList<EventLogEntry> filteredLogEntries = logEntries.filtered(null);

    private final ObservableList<GlcdFont> glcdFontList = FXCollections.observableArrayList(GlcdFont.values());

    private PixelBuffer frameBuffer;

    private ObservableList<GlcdBufferType> bufferStrategyList = FXCollections.observableArrayList(GlcdBufferType.values());

    private BufferLayout bufferLayout;

    private GlcdDriverEventHandler eventHandler = event -> {
        String name = event.getMessage().name();
        String desc = event.getMessage().getDescription();
        String hexValue = "0x" + ByteUtils.toHexString((byte) event.getValue());
        int decValue = Byte.toUnsignedInt((byte) event.getValue());
        logEntries.add(new EventLogEntry(eventIndex.getAndIncrement(), name, desc, hexValue, decValue));
        numOfBytes.incrementAndGet();
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        snackbar = new JFXSnackbar(vbRoot);

        cbBufferStrategy.setItems(bufferStrategyList);

        log.debug("Initializing developer controller");
        gpParams.getChildren().clear();

        ObservableList<GlcdDisplayDetails> displayList = FXCollections.observableArrayList();
        ObservableList<Method> methodList = FXCollections.observableArrayList();

        populateDisplays(displayList);
        populateOperations(methodList);

        ColumnConstraints alignLabel = new ColumnConstraints();
        alignLabel.setHalignment(HPos.CENTER);
        gpParams.getColumnConstraints().add(0, alignLabel);
        gpParams.getColumnConstraints().add(1, alignLabel);

        cbDisplay.getSelectionModel().selectedItemProperty().addListener(this::onDisplayChange);

        btnInvoke.setOnAction(this::invokeCurrentMethod);
        cbDrawOperation.setCellFactory(new Callback<ListView<Method>, ListCell<Method>>() {
            @Override
            public ListCell<Method> call(ListView<Method> param) {
                return new TextFieldListCell<Method>() {
                    @Override
                    public void updateItem(Method item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            List<String> paramString = Arrays.stream(item.getParameters()).map(p -> p.getType().getSimpleName()).collect(Collectors.toList());
                            setText(item.getName() + "(" + StringUtils.join(paramString.toArray(new String[]{}), ", ") + ")");
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        selectedDisplay.bind(cbDisplay.getSelectionModel().selectedItemProperty());
        selectedMethod.bind(cbDrawOperation.getSelectionModel().selectedItemProperty());
        cbDrawOperation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                log.debug("Method changed: {}", newValue);
                currentMethod = createDetails(newValue);
                updateParamsPane(currentMethod);
            }
        });

        FXCollections.sort(methodList, Comparator.comparing(Method::getName));

        cbDrawOperation.setItems(methodList);
        cbDisplay.setItems(displayList);

        TableColumn<EventLogEntry, Integer> eventIndex = new TableColumn<>("Index");
        eventIndex.setCellValueFactory(new PropertyValueFactory<>("eventIndex"));
        TableColumn<EventLogEntry, String> eventNameCol = new TableColumn<>("Event");
        eventNameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventNameCol.setPrefWidth(200.0d);
        TableColumn<EventLogEntry, String> eventDesc = new TableColumn<>("Description");
        eventDesc.setCellValueFactory(new PropertyValueFactory<>("eventDescription"));
        eventDesc.setPrefWidth(200.0d);
        TableColumn<EventLogEntry, String> valueHexCol = new TableColumn<>("Value (Hex)");
        valueHexCol.setCellValueFactory(new PropertyValueFactory<>("valueHex"));
        TableColumn<EventLogEntry, Integer> valueDecCol = new TableColumn<>("Value (Dec)");
        valueDecCol.setCellValueFactory(new PropertyValueFactory<>("valueDec"));

        //noinspection unchecked
        tvEventLog.getColumns().addAll(eventIndex, eventNameCol, eventDesc, valueHexCol, valueDecCol);

        cbDefaultFont.setItems(glcdFontList);
        tvEventLog.setItems(filteredLogEntries);
        btnClearLogs.setOnAction(event -> logEntries.clear());
        btnExportCsv.setOnAction(this::exportOutputToCSV);
        cbDefaultFont.disableProperty().bind(checkDefaultFont.selectedProperty().not());

        lvFilters.setItems(FXCollections.observableArrayList());
        lvFilters.getItems().addListener((InvalidationListener) observable -> updateLogFilters());
        lvFilters.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ContextMenu filterMenu = new ContextMenu();
        MenuItem removeFilterMenu = new MenuItem();
        MenuItem clearFiltersMenu = new MenuItem();
        removeFilterMenu.setText("Remove Filter(s)");
        removeFilterMenu.setOnAction(e -> {
            for (U8g2Message selectedFilter : lvFilters.getSelectionModel().getSelectedItems()) {
                log.debug("Removing filter: {}", selectedFilter);
                lvFilters.getItems().remove(selectedFilter);
            }
        });

        clearFiltersMenu.setText("Clear All");
        clearFiltersMenu.setOnAction(e -> lvFilters.getItems().clear());
        filterMenu.getItems().add(removeFilterMenu);
        filterMenu.getItems().add(clearFiltersMenu);

        lvFilters.setContextMenu(filterMenu);
        btnAddFilter.setOnAction(event -> {
            U8g2Message selectedMessage = cbFilters.getSelectionModel().getSelectedItem();
            if (selectedMessage != null && !lvFilters.getItems().contains(selectedMessage))
                lvFilters.getItems().add(selectedMessage);
        });

        cbFilters.setItems(FXCollections.observableArrayList(Arrays.stream(U8g2Message.values()).filter(p -> p.getType() == 1).collect(Collectors.toList())));

        cbDisplay.getSelectionModel().select(0);

        filterType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                updateLogFilters();
        });

        cbDefaultFont.getSelectionModel().select(0);

        ContextMenu eventLogMenu = new ContextMenu();
        MenuItem miCopyValuesHex = new MenuItem("Copy HEX value(s)");
        miCopyValuesHex.setUserData("hex");
        miCopyValuesHex.setOnAction(this::copySelectedValues);
        MenuItem miCopyValuesDec = new MenuItem("Copy DEC value(s)");
        miCopyValuesDec.setOnAction(this::copySelectedValues);
        miCopyValuesDec.setUserData("dec");
        MenuItem miCountBytes = new MenuItem("Count");
        miCountBytes.setOnAction(event -> {
            DialogUtil.showInfo("Total selected bytes: " + tvEventLog.getSelectionModel().getSelectedItems().size(), "");
        });

        eventLogMenu.getItems().addAll(miCopyValuesHex, miCopyValuesDec, miCountBytes);

        tvEventLog.setContextMenu(eventLogMenu);
        tvEventLog.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        screenOutput.setInactivePixelColor(Color.WHITE);
        screenOutput.setActivePixelColor(Color.WHITE);
        screenOutput.setBacklightColor(Color.BLACK);
        screenOutput.setPixelSize(3.10d);
    }

    private void onDisplayChange(ObservableValue observable, GlcdDisplayDetails oldValue, GlcdDisplayDetails newValue) {
        GlcdDisplay display = newValue.display;

        log.debug("Initializing virtual driver...");
        int displayWidth = display.getDisplaySize().getDisplayWidth();
        int displayHeight = display.getDisplaySize().getDisplayHeight();

        virtualDriver = DriverFactory.createVirtual(display, newValue.busInterface, eventHandler);
        log.debug("Frame buffer dimensions updated (Width = {}, Height = {})", displayWidth, displayHeight);

        if (frameBuffer == null) {
            frameBuffer = new PixelBuffer(displayWidth, displayHeight);
        } else {
            frameBuffer.resize(displayWidth, displayHeight);
        }

        refreshBufferStrategy();

        screenOutput.setBuffer(frameBuffer);
        if (screenOutput.isRunning())
            screenOutput.start();
    }

    private void refreshBufferStrategy() {
        if (checkUseBufferStrategy.isSelected()) {
            log.debug("Using custom buffer strategy: {}", cbBufferStrategy.getSelectionModel().getSelectedItem());
            bufferLayout = BufferLayoutFactory.createBufferLayout(cbBufferStrategy.getSelectionModel().getSelectedItem());
        } else {
            log.debug("Using default buffer strategy: {}", GlcdBufferType.VERTICAL);
            bufferLayout = BufferLayoutFactory.createBufferLayout(GlcdBufferType.VERTICAL);
        }
        bufferLayout.setBuffer(frameBuffer);
        bufferLayout.initialize();
    }

    private void copySelectedValues(ActionEvent event) {
        if (tvEventLog.getSelectionModel().getSelectedItems().size() > 0) {
            String valueType = StringUtils.defaultString((String) ((MenuItem) event.getSource()).getUserData(), "hex");

            List<String> data = new ArrayList<>();
            for (EventLogEntry entry : tvEventLog.getSelectionModel().getSelectedItems()) {
                if (valueType.equals("hex")) {
                    data.add(entry.getValueHex());
                } else if (valueType.equals("dec")) {
                    data.add(String.valueOf(entry.getValueDec()));
                }
            }
            ClipboardContent content = new ClipboardContent();
            content.putString(String.join(", ", data));
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    private void updateLogFilters() {
        log.debug("Updating filters");
        filteredLogEntries.setPredicate(p -> {
            if (filterType.getSelectedToggle() != null) {
                if (filterType.getSelectedToggle().getUserData().equals("include")) {
                    return lvFilters.getItems().stream().anyMatch(i -> i.name().equals(p.getEventName()));
                }
            }
            return lvFilters.getItems().stream().noneMatch(i -> i.name().equals(p.getEventName()));
        });
    }

    private void exportOutputToCSV(ActionEvent event) {
        if (logEntries.size() > 0) {
            File csvFile = exportToCSV("", null);
            if (csvFile != null) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true));
                    writer.append("Index, Event, Description, Value (HEX), Value (DEC)\n");
                    for (EventLogEntry entry : logEntries) {
                        writer.append(String.valueOf(entry.getEventIndex()));
                        writer.append(", ");
                        writer.append(entry.getEventName());
                        writer.append(", ");
                        writer.append(entry.getEventDescription());
                        writer.append(", ");
                        writer.append(entry.getValueHex());
                        writer.append(", ");
                        writer.append(String.valueOf(entry.getValueDec()));
                        writer.append("\n");
                    }
                    log.info("Exported event log to CSV file '{}'", csvFile.getAbsolutePath());
                    writer.flush();
                } catch (IOException e) {
                    throw new ExportCSVException(e);
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private File exportToCSV(String initialDir, Consumer<String> saveLastPathTo) {
        File file = null;
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export to CSV");

            fileChooser.setInitialFileName(csvFileNameFormatter.format(ZonedDateTime.now()));
            if (!StringUtils.isBlank(initialDir)) {
                fileChooser.setInitialDirectory(new File(initialDir));
            } else {
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            }
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "csv"));
            file = fileChooser.showSaveDialog(Context.getPrimaryStage());
            return file;
        } finally {
            if (file != null && saveLastPathTo != null) {
                saveLastPathTo.accept(FilenameUtils.getFullPath(file.getAbsolutePath()));
            }
        }
    }

    private void invokeCurrentMethod(ActionEvent event) {
        if (virtualDriver == null) {
            DialogUtil.showWarning("Display controller not selected", "Please select a display controller first", Context.getPrimaryStage());
            return;
        }
        if (currentMethod != null) {
            log.info("Invoking method: {}", currentMethod.method);
            for (MethodParam arg : currentMethod.arguments) {
                log.debug("> Param = {}, Value = {}, Type = {}", arg.parameter.getName(), arg.value.getValue(), arg.parameter.getType());
            }
            try {
                eventIndex.set(0);
                logEntries.clear();

                if (checkDefaultFont.isSelected()) {
                    GlcdFont defaultFont = cbDefaultFont.getSelectionModel().getSelectedItem();
                    if (defaultFont != null) {
                        log.info("Setting default font before invocation: {}", defaultFont);
                        virtualDriver.setFont(defaultFont);
                    } else {
                        log.info("No default font specified");
                    }
                }

                refreshBufferStrategy();

                virtualDriver.clearBuffer();
                Object retval = currentMethod.method.invoke(virtualDriver, currentMethod.toValueArgs());

                if (cbSendBuffer.isSelected()) {
                    virtualDriver.sendBuffer();
                }

                refreshOutput(virtualDriver.getBuffer());
                snackbar.show("Method invoked successfully" + ((retval != null) ? " (Return Value: " + String.valueOf(retval) + ")" : ""), 5000);
                lblBytesReceived.setText(String.valueOf(numOfBytes.getAndSet(0)));
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Invocation error", e);
                String message;
                if (e.getCause() != null && !StringUtils.isBlank(e.getCause().getMessage())) {
                    message = e.getCause().getMessage();
                } else {
                    if (!StringUtils.isBlank(e.getMessage())) {
                        message = e.getMessage();
                    } else {
                        message = e.getClass().getSimpleName();
                    }
                }
                DialogUtil.showError(e.getClass().getSimpleName(), message, Stages.getDeveloperStage());
            }
        }
    }

    private void refreshOutput(byte[] buffer) {
        log.info("Refreshing framebuffer with {} bytes of data", buffer.length);
        screenOutput.start();
        frameBuffer.reset();
        bufferLayout.reset();
        for (byte data : buffer) {
            bufferLayout.processByte(data);
        }
    }

    private MethodDetails createDetails(Method method) {
        MethodDetails details = new MethodDetails();
        details.method = method;

        for (Parameter param : method.getParameters()) {
            MethodParam mParam = new MethodParam();
            mParam.parameter = param;
            details.arguments.add(mParam);
        }
        return details;
    }

    private void updateParamsPane(MethodDetails details) {
        int row = 0;
        gpParams.getChildren().clear();

        try {
            int argIndex = 0;

            boolean autogen = checkAutoGen.isSelected();

            for (MethodParam param : details.arguments) {
                log.debug("\tParam = {}, Type: {}", param.parameter.getName(), param.parameter.getType());
                gpParams.add(new Label(param.parameter.getName() + " (" + param.parameter.getType().getSimpleName() + ")"), 0, row);

                Node valueField;
                MethodParam methodParam = details.arguments.get(argIndex++);

                Class<?> paramType = param.parameter.getType();

                if (paramType.isEnum()) {
                    JFXComboBox comboBox = new JFXComboBox();
                    methodParam.value.bind(comboBox.getSelectionModel().selectedItemProperty());
                    Field enumField = param.parameter.getType().getDeclaredField("$VALUES");
                    enumField.setAccessible(true);
                    Enum[] e = (Enum[]) enumField.get(null);
                    //noinspection unchecked
                    comboBox.setItems(FXCollections.observableArrayList(e));
                    comboBox.getSelectionModel().select(RandomUtils.nextInt(0, comboBox.getItems().size() - 1));
                    valueField = comboBox;
                } else if (paramType.equals(File.class)) {
                    GridPane gpFileChooser = new GridPane();
                    JFXTextField textField = new JFXTextField("Select File");
                    textField.setEditable(false);
                    textField.setMinWidth(150);
                    JFXButton button = new JFXButton("...");
                    button.setOnAction(event -> {
                        File file = FileUtils.openFileFromDialog("Open file", "", new FileChooser.ExtensionFilter("File", "*.*"), null);
                        if (file != null) {
                            textField.setText(file.getAbsolutePath());
                            methodParam.value.set(file);
                        }
                    });
                    gpFileChooser.add(textField, 0, 0);
                    gpFileChooser.add(button, 1, 0);
                    valueField = gpFileChooser;
                } else if (paramType.equals(boolean.class)) {
                    JFXComboBox<Boolean> comboBox = new JFXComboBox<>();
                    comboBox.setItems(FXCollections.observableArrayList(true, false));
                    methodParam.value.bind(comboBox.getSelectionModel().selectedItemProperty());
                    comboBox.getSelectionModel().select(RandomUtils.nextInt(0, comboBox.getItems().size() - 1));
                    valueField = comboBox;
                } else if (paramType.equals(byte[].class)) {
                    JFXTextField textField = new JFXTextField();
                    textField.setPromptText("Enter base64 encoded string");
                    methodParam.value.bind(textField.textProperty());
                    valueField = textField;
                } else {
                    JFXTextField textField = new JFXTextField();
                    methodParam.value.bind(textField.textProperty());

                    if (autogen) {
                        if (isNumericType(paramType)) {
                            textField.setText(String.valueOf(RandomUtils.nextInt(1, 100)));
                        } else if (paramType.equals(String.class)) {
                            textField.setText("test string");
                        }
                    }

                    valueField = textField;
                }

                //noinspection CastCanBeRemovedNarrowingVariableType
                ((Region) valueField).setMinWidth(200);
                //noinspection CastCanBeRemovedNarrowingVariableType
                ((Region) valueField).setPrefWidth(200);
                gpParams.add(valueField, 1, row++);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean isNumericType(Class<?> cls) {
        if (cls.isPrimitive()) {
            return primitiveNumbers.contains(cls);
        } else {
            return Number.class.isAssignableFrom(cls);
        }
    }

    private void populateOperations(ObservableList<Method> operationList) {
        List<Method> availableMethods = Arrays.stream(GlcdDriver.class.getMethods())
                .filter(this::isValid)
                .collect(Collectors.toList());
        operationList.addAll(availableMethods);
    }

    private boolean isValid(Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers))
            return false;

        String[] excluded = new String[]{"equals", "wait", "notify", "write", "hashCode", "toString", "getClass", "notifyAll", "getId", "getConfig", "getDriverEventHandler"};

        if (Arrays.stream(excluded).anyMatch(p -> p.equals(method.getName()))) {
            log.debug("Excluded: {}", method.getName());
            return false;
        }
        return true;
    }

    private void populateDisplays(ObservableList<GlcdDisplayDetails> displayList) {
        try {
            for (Class<?> iface : Glcd.class.getDeclaredClasses()) {
                for (Field f : iface.getDeclaredFields()) {
                    GlcdDisplay display = (GlcdDisplay) f.get(null);
                    for (GlcdSetupInfo setupInfo : display.getSetupDetails()) {
                        for (GlcdBusInterface bus : GlcdBusInterface.values()) {
                            boolean excluded = bus.getBusType().equals(GlcdBusType.SOFTWARE) && (bus.getDescription().contains("SPI") || bus.getDescription().contains("I2C") || bus.getDescription().contains("Serial"));
                            if (setupInfo.isSupported(bus) && !excluded) {
                                GlcdDisplayDetails details = new GlcdDisplayDetails();
                                details.display = display;
                                details.busInterface = bus;
                                displayList.add(details);
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private class MethodParam {
        private Parameter parameter;
        private ObjectProperty<Object> value = new SimpleObjectProperty<>();
    }

    private class MethodDetails {
        private Method method;
        private ArrayList<MethodParam> arguments = new ArrayList<>();

        Object[] toValueArgs() {
            Object[] args = new Object[arguments.size()];
            int index = 0;
            for (MethodParam param : arguments) {
                Object val;
                Object sourceValue = param.value.get();
                log.debug("toValueArgs() => Source value type = {}, Actual value type = {}", param.value.getValue().getClass().getSimpleName(), param.parameter.getType().getSimpleName());
                if (param.parameter.getType().equals(int.class)) {
                    val = Integer.valueOf((String) sourceValue);
                } else if (param.parameter.getType().equals(short.class)) {
                    val = Short.valueOf((String) sourceValue);
                } else if (param.parameter.getType().equals(long.class)) {
                    val = Long.valueOf((String) sourceValue);
                } else if (param.parameter.getType().equals(byte.class)) {
                    val = Byte.valueOf((String) sourceValue);
                } else if (param.parameter.getType().isEnum()) {
                    val = sourceValue;
                } else if (param.parameter.getType().equals(boolean.class)) {
                    val = sourceValue;
                } else if (param.parameter.getType().equals(byte[].class)) {
                    if (!StringUtils.isBlank((String) sourceValue)) {
                        String tmp = (String) sourceValue;
                        val = Base64.getDecoder().decode(tmp);
                    } else {
                        log.debug("Base64 value not specified. Using default: {}", DEFAULT_BASE64_VAL);
                        val = Base64.getDecoder().decode(DEFAULT_BASE64_VAL);
                    }
                } else {
                    val = param.value.get();
                }
                args[index++] = val;
            }
            return args;
        }
    }

    private class GlcdDisplayDetails {
        private GlcdDisplay display;

        private GlcdBusInterface busInterface;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(display.getController().name());
            sb.append(" :: ");
            sb.append(display.getDisplaySize().getDisplayWidth());
            sb.append("x");
            sb.append(display.getDisplaySize().getDisplayHeight());
            sb.append(" :: ");
            sb.append(busInterface.getDescription());
            return sb.toString();
        }
    }

    public class EventLogEntry {
        private final SimpleIntegerProperty eventIndex;
        private final SimpleStringProperty eventName;
        private final SimpleStringProperty eventDescription;
        private final SimpleStringProperty valueHex;
        private final SimpleIntegerProperty valueDec;

        private EventLogEntry(int eventIndex, String eventName, String eventDesc, String valueHex, int valueDec) {
            this.eventIndex = new SimpleIntegerProperty(eventIndex);
            this.eventName = new SimpleStringProperty(eventName);
            this.eventDescription = new SimpleStringProperty(eventDesc);
            this.valueHex = new SimpleStringProperty(valueHex);
            this.valueDec = new SimpleIntegerProperty(valueDec);
        }

        public int getEventIndex() {
            return eventIndex.get();
        }

        public SimpleIntegerProperty eventIndexProperty() {
            return eventIndex;
        }

        public void setEventIndex(int eventIndex) {
            this.eventIndex.set(eventIndex);
        }

        public String getEventDescription() {
            return eventDescription.get();
        }

        public SimpleStringProperty eventDescriptionProperty() {
            return eventDescription;
        }

        public void setEventDescription(String eventDescription) {
            this.eventDescription.set(eventDescription);
        }

        private String getEventName() {
            return eventName.get();
        }

        public SimpleStringProperty eventNameProperty() {
            return eventName;
        }

        private void setEventName(String eventName) {
            this.eventName.set(eventName);
        }

        private String getValueHex() {
            return valueHex.get();
        }

        public SimpleStringProperty valueHexProperty() {
            return valueHex;
        }

        private void setValueHex(String valueHex) {
            this.valueHex.set(valueHex);
        }

        private int getValueDec() {
            return valueDec.get();
        }

        public SimpleIntegerProperty valueDecProperty() {
            return valueDec;
        }

        private void setValueDec(int valueDec) {
            this.valueDec.set(valueDec);
        }
    }
}
