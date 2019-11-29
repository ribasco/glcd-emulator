/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
 * Filename: module-info.java
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
module glcd.simulator {
    exports com.ibasco.glcdemulator;
    exports com.ibasco.glcdemulator.utils to ch.qos.logback.core;
    exports com.ibasco.glcdemulator.model to commons.beanutils;
    opens com.ibasco.glcdemulator.controllers to javafx.fxml;
    exports com.ibasco.glcdemulator.controls to javafx.fxml;
    opens com.ibasco.glcdemulator.model to javafx.base;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires com.ibasco.ucgdisplay.ucgd.drivers.glcd;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires commons.cli;
    requires org.apache.commons.lang3;
    requires com.fazecast.jSerialComm;
    requires io.github.classgraph;
    requires org.apache.commons.io;
    requires commons.beanutils;
    requires java.desktop;
    requires javafx.swing;
    requires ch.qos.logback.core;
    requires org.controlsfx.controls;
    requires com.ibasco.ucgdisplay.ucgd.nativ.graphics;
    requires com.jfoenix;
    requires de.jensd.fx.glyphs.commons;
    requires de.jensd.fx.glyphs.fontawesome;
    requires de.jensd.fx.glyphs.controls;
    requires de.jensd.fx.glyphs.materialicons;
    requires de.jensd.fx.glyphs.materialdesignicons;
    requires gson;
    requires fx.gson;

}
