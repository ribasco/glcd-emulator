/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: SerialPortService.java
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
package com.ibasco.glcdemulator.services;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialPortService extends Service<ObservableList<SerialPort>> {

    private static final Logger log = LoggerFactory.getLogger(SerialPortService.class);

    private ReadOnlyListWrapper<SerialPort> serialPorts = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());

    @Override
    protected Task<ObservableList<SerialPort>> createTask() {
        return new Task<ObservableList<SerialPort>>() {
            @Override
            protected ObservableList<SerialPort> call() throws Exception {
                log.debug("Querying for serial ports");
                refreshSerialPorts();
                return serialPorts;
            }
        };
    }

    private void refreshSerialPorts() {
        Platform.runLater(() -> {
            SerialPort[] ports = SerialPort.getCommPorts();
            serialPorts.clear();
            if (ports.length > 0) {
                serialPorts.addAll(ports);
            }
        });
    }

    public SerialPort findSerialPortByName(String name) {
        if (serialPorts.size() == 0) {
            refreshSerialPorts();
        }
        return serialPorts.stream()
                .filter(p -> p.getSystemPortName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public ObservableList<SerialPort> getSerialPorts() {
        return serialPorts.get();
    }

    public ReadOnlyListProperty<SerialPort> serialPortsProperty() {
        return serialPorts.getReadOnlyProperty();
    }
}
