module glcd.simulator {
    exports com.ibasco.glcdemulator;
    exports com.ibasco.glcdemulator.utils to ch.qos.logback.core;
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

}