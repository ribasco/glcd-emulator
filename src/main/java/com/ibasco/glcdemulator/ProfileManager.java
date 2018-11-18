/*-
 * ========================START=================================
 * Organization: Rafael Luis Ibasco
 * Project: GLCD Emulator
 * Filename: ProfileManager.java
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
package com.ibasco.glcdemulator;

import com.ibasco.glcdemulator.model.GlcdConfigApp;
import com.ibasco.glcdemulator.model.GlcdEmulatorProfile;
import com.ibasco.glcdemulator.utils.BeanUtils;
import com.ibasco.glcdemulator.utils.FileUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
public class ProfileManager {

    //<editor-fold desc="Public static properties">
    public static final Logger log = LoggerFactory.getLogger(ProfileManager.class);

    public static final GlcdEmulatorProfile DEFAULT_PROFILE = new GlcdEmulatorProfile("default");

    public static final String DEFAULT_PROFILE_NAME = "default";

    public static final String DEFAULT_PROFILE_DIRECTORY = "profiles";

    public static final String PROFILE_PREFIX = "profile_";

    public static final String PROFILE_SUFFIX = ".json";

    public static final String DEFAULT_PROFILE_DIR_PATH = System.getProperty("user.dir") + File.separator + DEFAULT_PROFILE_DIRECTORY;

    public static final int DEFAULT_PROFILE_ID = 1;
    //</editor-fold>

    private ObservableList<GlcdEmulatorProfile> profiles = FXCollections.observableArrayList();

    private FilteredList<GlcdEmulatorProfile> filteredProfiles = new FilteredList<>(profiles);

    private ObjectProperty<GlcdEmulatorProfile> activeProfile = new SimpleObjectProperty<>();

    private GlcdConfigApp appConfig = Context.getInstance().getAppConfig();

    private ConfigManager configService = Context.getInstance().getConfigService();

    public static class Predicates {
        public static Predicate<GlcdEmulatorProfile> byId(int id) {
            return p -> p.getId() == id;
        }

        public static Predicate<GlcdEmulatorProfile> byName(String name) {
            return p -> p.getName().equalsIgnoreCase(name);
        }

        public static Predicate<GlcdEmulatorProfile> containsName(String name) {
            return p -> p.getName().contains(name);
        }

        public static Predicate<GlcdEmulatorProfile> modified() {
            return p -> Context.getInstance().getProfileManager().isModified(p);
        }
    }

    ProfileManager() {
    }

    public int newUniqueId() {
        int newId = appConfig.nextProfileId();
        while (getProfile(newId) != null) {
            newId = appConfig.nextProfileId();
        }
        return newId;
    }

    public boolean isModified(GlcdEmulatorProfile profile) {
        //query original copy
        if (profile.isNew())
            return true;
        GlcdEmulatorProfile origCopy = Context.getInstance().getProfileManager().getProfileFromFS(profile.getFile());
        return !BeanUtils.diff(origCopy, profile).isEmpty();
    }

    public GlcdEmulatorProfile create(String newName, GlcdEmulatorProfile baseProfile) {
        GlcdEmulatorProfile newProfile = new GlcdEmulatorProfile(baseProfile);
        newProfile.setId(newUniqueId());
        if (!StringUtils.isBlank(newName))
            newProfile.setName(newName);
        else {
            newProfile.setName(createNameFromId(newProfile));
        }
        return newProfile;
    }

    public void delete(GlcdEmulatorProfile profile) {
        profiles.remove(profile);
    }

    public void save(GlcdEmulatorProfile profile) {
        try {
            if (profile.getFile() == null) {
                String newFileName = createProfilePath(profile);
                log.debug("Saving new profile with filename = {}", newFileName);
                profile.setFile(new File(newFileName));
            }
            configService.save(profile, profile.getFile());
        } catch (IOException e) {
            log.error("Unable to save active profile", e);
        }
    }

    public void save() {
        save(activeProfile.get());
    }

    public void refresh() {
        String profileDirPath = appConfig.getProfileDirPath();
        FileUtils.ensureDirectoryExistence(profileDirPath);
        profiles.clear();
        profiles.addAll(getProfilesFromFS());
    }

    public boolean exists(String profileName) {
        return find(Predicates.byName(profileName)) != null;
    }

    public boolean exists(GlcdEmulatorProfile profile) {
        return find(Predicates.byId(profile.getId())) != null;
    }

    public GlcdEmulatorProfile find(Predicate<GlcdEmulatorProfile> predicate) {
        return getProfiles().stream().filter(predicate).findFirst().orElse(null);
    }

    public List<GlcdEmulatorProfile> findList(Predicate<GlcdEmulatorProfile> predicate) {
        return getProfiles().filtered(predicate);
    }

    public GlcdEmulatorProfile create(String newName) {
        return create(newName, this.activeProfile.get());
    }

    public ObservableList<GlcdEmulatorProfile> getProfiles() {
        if (profiles.isEmpty())
            refresh();
        return profiles;
    }

    public GlcdEmulatorProfile getActiveProfile() {
        return activeProfile.get();
    }

    public ObjectProperty<GlcdEmulatorProfile> activeProfileProperty() {
        return activeProfile;
    }

    public void setActiveProfile(GlcdEmulatorProfile activeProfile) {
        this.activeProfile.set(activeProfile);
    }

    public ReadOnlyObjectProperty<ObservableList<GlcdEmulatorProfile>> filteredProfilesProperty() {
        return new SimpleObjectProperty<>(filteredProfiles);
    }

    public ObjectProperty<Predicate<? super GlcdEmulatorProfile>> filterProperty() {
        return filteredProfiles.predicateProperty();
    }

    public List<GlcdEmulatorProfile> getProfilesFromFS() {
        ArrayList<GlcdEmulatorProfile> profiles = new ArrayList<>();
        try {
            String profileDirPath = appConfig.getProfileDirPath();
            FileUtils.ensureDirectoryExistence(profileDirPath);
            Files.list(new File(profileDirPath).toPath()).forEach(path -> {
                GlcdEmulatorProfile profile = getProfileFromFS(path.toFile());
                profiles.add(profile);
            });
        } catch (IOException e) {
            log.error("An error occured while retrieving the profiles", e);
        }
        return profiles;
    }

    public GlcdEmulatorProfile getProfile(int profileId) {
        return find(Predicates.byId(profileId));
    }

    public GlcdEmulatorProfile getProfileFromFS(GlcdEmulatorProfile profile) {
        return getProfileFromFS(profile.getFile());
    }

    public GlcdEmulatorProfile getProfileFromFS(File profile) {
        if (profile == null)
            throw new IllegalArgumentException("File cannot be null");
        GlcdEmulatorProfile profileFromFs = configService.getConfig(profile, GlcdEmulatorProfile.class);
        return profileFromFs;
    }

    public GlcdEmulatorProfile getProfileFromFS(int profileId) {
        GlcdEmulatorProfile profile = getProfilesFromFS().stream().filter(p -> p.getId() == profileId).findFirst().orElse(null);
        return profile;
    }

    public String createNameFromId(GlcdEmulatorProfile profile) {
        if (!exists(profile.getName()))
            return profile.getName();
        int nameCounter = 1;
        String referenceName = StringUtils.trim(profile.getName().replaceAll("\\([0-9]*\\)", ""));
        String newName;
        do {
            newName = StringUtils.capitalize(referenceName) + " (" + nameCounter++ + ")";
        } while (exists(newName));
        return newName;
    }

    private String createDefaultProfileName(GlcdEmulatorProfile profile) {
        if (profile == null)
            throw new IllegalArgumentException("Profile cannot be null");
        return "glcd_" +
                profile.getDisplaySizeWidth() +
                "x" +
                profile.getDisplaySizeHeight() +
                "_" +
                (int) profile.getLcdPixelSize() +
                "px";
    }

    private String createProfilePath(GlcdEmulatorProfile profile) {
        if (profile == null)
            throw new IllegalArgumentException("Profile argument must not be null");
        return createProfilePath(profile.getName());
    }

    private String createProfilePath(String name) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("Profile name must not be empty");
        String dirPath = appConfig.getProfileDirPath();
        return dirPath + File.separator + PROFILE_PREFIX + name.toLowerCase() + PROFILE_SUFFIX;
    }
}
