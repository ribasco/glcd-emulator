package com.ibasco.glcdemulator.controllers;

import com.ibasco.glcdemulator.Controller;
import com.ibasco.glcdemulator.DriverFactory;
import com.ibasco.glcdemulator.utils.ByteUtils;
import com.ibasco.ucgdisplay.drivers.glcd.*;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusInterface;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdBusType;
import com.ibasco.ucgdisplay.drivers.glcd.enums.GlcdFont;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GlcdDeveloperController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(GlcdDeveloperController.class);

    @FXML
    private JFXComboBox<GlcdDisplayDetails> cbDisplay;

    @FXML
    private JFXComboBox<Method> cbDrawOperation;

    @FXML
    private GridPane gpParams;

    @FXML
    private JFXButton btnInvoke;

    @FXML
    private JFXCheckBox cbSendBuffer;

    @FXML
    private TableView<EventLogEntry> tvEventLog;

    @FXML
    private JFXComboBox<GlcdFont> cbFont;

    private ObjectProperty<Method> selectedMethod = new SimpleObjectProperty<>();

    private ObjectProperty<GlcdDisplayDetails> selectedDisplay = new SimpleObjectProperty<>();

    private MethodDetails currentMethod;

    private ObservableList<GlcdFont> fontList = FXCollections.observableArrayList(GlcdFont.values());

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
                log.error("Source value type = {}, Actual value type = {}", param.value.getValue().getClass().getSimpleName(), param.parameter.getType().getSimpleName());
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

    private GlcdDriver virtualDriver;

    private ObservableList<EventLogEntry> logEntries = FXCollections.observableArrayList();

    private GlcdDriverEventHandler eventHandler = event -> {
        String name = event.getMessage().name();
        String desc = event.getMessage().getDescription();
        String hexValue = ByteUtils.toHexString((byte) event.getValue());
        int decValue = Byte.toUnsignedInt((byte) event.getValue());
        logEntries.add(new EventLogEntry(name, desc, hexValue, decValue));

    };

    public class EventLogEntry {
        private final SimpleStringProperty eventName;
        private final SimpleStringProperty eventDescription;
        private final SimpleStringProperty valueHex;
        private final SimpleIntegerProperty valueDec;

        private EventLogEntry(String eventName, String eventDesc, String valueHex, int valueDec) {
            this.eventName = new SimpleStringProperty(eventName);
            this.eventDescription = new SimpleStringProperty(eventDesc);
            this.valueHex = new SimpleStringProperty(valueHex);
            this.valueDec = new SimpleIntegerProperty(valueDec);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        ObservableList<GlcdDisplayDetails> displayList = FXCollections.observableArrayList();
        ObservableList<Method> methodList = FXCollections.observableArrayList();

        populateDisplays(displayList);
        populateOperations(methodList);

        ColumnConstraints alignLabel = new ColumnConstraints();
        alignLabel.setHalignment(HPos.CENTER);
        gpParams.getColumnConstraints().add(0, alignLabel);
        gpParams.getColumnConstraints().add(1, alignLabel);

        cbDisplay.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GlcdDisplayDetails>() {
            @Override
            public void changed(ObservableValue<? extends GlcdDisplayDetails> observable, GlcdDisplayDetails oldValue, GlcdDisplayDetails newValue) {
                log.debug("Initializing virtual driver...");
                virtualDriver = DriverFactory.createVirtual(newValue.display, newValue.busInterface, eventHandler);
            }
        });

        btnInvoke.setOnAction(this::invokeCurrentMethod);
        cbDrawOperation.setConverter(new StringConverter<Method>() {
            @Override
            public String toString(Method object) {
                StringBuilder params = new StringBuilder();
                return object.getName() + "(" + StringUtils.join(object.getParameters(), ", ") + ")";
            }

            @Override
            public Method fromString(String string) {
                return null;
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

        cbDrawOperation.setItems(methodList);
        cbDisplay.setItems(displayList);
        cbFont.setItems(fontList);

        TableColumn<EventLogEntry, String> eventNameCol = new TableColumn<>("Event");
        eventNameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        TableColumn<EventLogEntry, String> eventDesc = new TableColumn<>("Description");
        eventDesc.setCellValueFactory(new PropertyValueFactory<>("eventDescription"));
        TableColumn<EventLogEntry, String> valueHexCol = new TableColumn<>("Value (Hex)");
        valueHexCol.setCellValueFactory(new PropertyValueFactory<>("valueHex"));
        TableColumn<EventLogEntry, Integer> valueDecCol = new TableColumn<>("Value (Dec)");
        valueDecCol.setCellValueFactory(new PropertyValueFactory<>("valueDec"));

        //noinspection unchecked
        tvEventLog.getColumns().addAll(eventNameCol, eventDesc, valueHexCol, valueDecCol);

        tvEventLog.setItems(logEntries);
    }

    private void invokeCurrentMethod(ActionEvent event) {
        if (currentMethod != null) {
            log.info("Invoking method: {}", currentMethod.method);
            for (MethodParam arg : currentMethod.arguments) {
                log.info("Param = {}, Value = {}, Type = {}", arg.parameter.getName(), arg.value.getValue(), arg.parameter.getType());
            }
            try {
                logEntries.clear();
                currentMethod.method.invoke(virtualDriver, currentMethod.toValueArgs());
                if (cbSendBuffer.isSelected()) {
                    virtualDriver.sendBuffer();
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Invocation error", e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invocation error");
                alert.setHeaderText(e.getMessage());
                if (e.getCause() != null) {
                    alert.setContentText(e.getCause().getMessage());
                }
                alert.showAndWait();
            }
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
            for (MethodParam param : details.arguments) {
                log.debug("\tParam = {}, Type: {}", param.parameter.getName(), param.parameter.getType());
                gpParams.add(new Label(param.parameter.getName() + " (" + param.parameter.getType().getSimpleName() + ")"), 0, row);

                Node valueField;

                if (param.parameter.getType().isEnum()) {
                    JFXComboBox comboBox = new JFXComboBox();
                    details.arguments.get(argIndex++).value.bind(comboBox.getSelectionModel().selectedItemProperty());
                    Field enumField = param.parameter.getType().getDeclaredField("$VALUES");
                    enumField.setAccessible(true);
                    Enum[] e = (Enum[]) enumField.get(null);
                    //noinspection unchecked
                    comboBox.setItems(FXCollections.observableArrayList(e));
                    valueField = comboBox;
                } else {
                    JFXTextField textField = new JFXTextField();
                    details.arguments.get(argIndex++).value.bind(textField.textProperty());
                    valueField = textField;
                }
                ((Control) valueField).setPrefWidth(100);
                ((Control) valueField).setMinWidth(100);
                gpParams.add(valueField, 1, row++);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
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
}
