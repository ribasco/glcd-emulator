/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Simulator
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
package com.ibasco.glcdemulator.controllers;

import com.ibasco.glcdemulator.Context;
import com.ibasco.glcdemulator.Controller;
import com.ibasco.glcdemulator.Stages;
import com.ibasco.glcdemulator.constants.Common;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ibasco.glcdemulator.utils.ResourceUtil.readFileResource;

public class GlcdAboutController extends Controller {

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
}
