/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: DialogUtil.java
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
package com.ibasco.glcdemulator.utils;

import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.Stages;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.function.Consumer;

public class DialogUtil {

    public static final ButtonType REPORT_BUTTON = new ButtonType("Report Issue", ButtonBar.ButtonData.OK_DONE);

    public static boolean promptConfirmation(String header, String content) {
        return promptConfirmation(header, content, null);
    }

    public static boolean promptConfirmation(String header, String content, Window owner) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        confirmAlert.setHeaderText(header);
        confirmAlert.setTitle(header);
        if (owner == null)
            owner = Stages.getPrimaryStage();
        confirmAlert.initOwner(owner);
        confirmAlert.getDialogPane().getStylesheets().add(Context.getPrimaryStage().getScene().getStylesheets().get(0));
        Optional<ButtonType> answer = confirmAlert.showAndWait();
        return answer.isPresent() && answer.get().equals(ButtonType.YES);
    }

    public static void showError(String title, String header, Window owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.initOwner(Stages.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static void showWarning(String title, String header, Window owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
        alert.initOwner(Stages.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static Alert createExceptionDialog(String title, String header, Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK, REPORT_BUTTON);
        alert.initOwner(Stages.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(ex.getCause() != null ? "Cause: " + ex.getCause().getMessage() : "");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setStyle("-fx-font-family: \"Courier New\"; -fx-font-size: 12px;");
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);

        return alert;
    }

    public static void showInfo(String header, String content) {
        showInfo(header, content, Stages.getPrimaryStage());
    }

    public static void showInfo(String header, String content, Window owner) {
        Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        confirmAlert.setHeaderText(header);
        if (owner == null)
            owner = Stages.getPrimaryStage();
        confirmAlert.initOwner(owner);
        confirmAlert.showAndWait();
    }

    public static Alert createAlertDialogWithCheckbox(Alert.AlertType type, String title, String header, String content, String optOutMessage, Consumer<Boolean> optOutAction, ButtonType... buttonTypes) {
        return createAlertDialogWithCheckbox(type, title, header, content, optOutMessage, optOutAction, Context.getPrimaryStage(), buttonTypes);
    }

    public static Alert createAlertDialogWithCheckbox(Alert.AlertType type, String title, String header, String content, String optOutMessage, Consumer<Boolean> optOutAction, Window owner, ButtonType... buttonTypes) {
        Alert alert = new Alert(type);
        // Need to force the alert to layout in order to grab the graphic,
        // as we are replacing the dialog pane with a custom pane
        alert.getDialogPane().applyCss();
        Node graphic = alert.getDialogPane().getGraphic();
        alert.setDialogPane(new DialogPane() {
            @Override
            protected Node createDetailsButton() {
                CheckBox optOut = new CheckBox();
                optOut.setText(optOutMessage);
                optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
                return optOut;
            }
        });
        alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
        alert.getDialogPane().setExpandableContent(new Group());
        alert.getDialogPane().setGraphic(graphic);
        alert.getDialogPane().setExpanded(true);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }
}
