/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: GlcdAboutController.java
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
package com.ibasco.glcdemu.controllers;

import com.ibasco.glcdemu.Context;
import com.ibasco.glcdemu.GlcdController;
import com.ibasco.glcdemu.Stages;
import com.ibasco.glcdemu.constants.Common;
import com.ibasco.glcdemu.utils.ResourceUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class GlcdAboutController extends GlcdController {

    private static final Logger log = LoggerFactory.getLogger(GlcdAboutController.class);

    @FXML
    private JFXTabPane tpAbout;

    @FXML
    private JFXTextArea taLicense;

    @FXML
    private Hyperlink linkWebsite;

    @FXML
    private Hyperlink linkEmail;

    @FXML
    private JFXButton btnClose;

    @FXML
    private JFXButton btnDonate;

    @FXML
    private Label lblLicenseShort;

    @FXML
    private Label lblVersion;

    @FXML
    private JFXTextArea taThirdParty;

    @Override
    protected void initializeOnce() {
        String licenseData = readFileResource("license.txt");
        String thirdPartyData = readFileResource("third-party.txt");

        taThirdParty.setText(thirdPartyData);
        taLicense.setText(licenseData);
        lblLicenseShort.setText(readFileResource("license-short.txt"));
        taThirdParty.setWrapText(false);
        btnDonate.setOnAction(event -> Context.getInstance().getHostServices().showDocument(Common.DONATE_URL));
        linkWebsite.setOnAction(event -> Context.getInstance().getHostServices().showDocument(Common.PROJECT_URL));
        linkEmail.setOnAction(event -> Context.getInstance().getHostServices().showDocument("mailto: " + Common.DEVELOPER_EMAIL));
        btnClose.setOnAction(event -> Stages.getAboutStage().close());
        lblVersion.setText(Context.getAppVersion());

    }

    private String readFileResource(String fileName) {
        try {
            URI licenseFile = ResourceUtil.getResource(fileName).toURI();
            String data = FileUtils.readFileToString(new File(licenseFile), StandardCharsets.UTF_8);
            return data;
        } catch (URISyntaxException | IOException e) {
            log.error("Could not read file resource", e);
        }
        return null;
    }
}
