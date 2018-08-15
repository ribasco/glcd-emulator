package com.ibasco.glcdemu.services;

import com.ibasco.glcdemu.beans.GlcdConfigEmulatorProfile;
import com.ibasco.glcdemu.utils.BeanUtils;
import com.ibasco.glcdemu.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.WatchService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("WeakerAccess")
public class GlcdConfigProfileService {

    public static final Logger log = LoggerFactory.getLogger(GlcdConfigProfileService.class);

    private static final GlcdConfigEmulatorProfile DEFAULT_PROFILE = new GlcdConfigEmulatorProfile("default");

    public static final String DEFAULT_PROFILE_NAME = "default";

    private static final String DEFAULT_PROFILE_DIRECTORY = "profiles";

    private static final String PROFILE_PREFIX = "profile_";

    private static final String PROFILE_SUFFIX = ".json";

    public static final String DEFAULT_PROFILE_FNAME = PROFILE_PREFIX + DEFAULT_PROFILE_NAME + PROFILE_SUFFIX;

    public static final String DEFAULT_PROFILE_DIR_PATH = System.getProperty("user.dir") + File.separator + DEFAULT_PROFILE_DIRECTORY;

    public static final int DEFAULT_PROFILE_ID = 1;

    private ObservableList<GlcdConfigEmulatorProfile> profiles = FXCollections.observableArrayList();

    private GlcdConfigEmulatorProfile activeProfile;

    public static class Predicates {
        public static Predicate<GlcdConfigEmulatorProfile> byId(int id) {
            return p -> p.getId() == id;
        }

        public static Predicate<GlcdConfigEmulatorProfile> byName(String name) {
            return p -> p.getName().equalsIgnoreCase(name);
        }

        public static Predicate<GlcdConfigEmulatorProfile> containsName(String name) {
            return p -> p.getName().contains(name);
        }
    }

    public GlcdConfigProfileService() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            log.error("Unable to initialize file watch service", e);
        }
    }

    public boolean isDefault(GlcdConfigEmulatorProfile profile) {
        int defaultProfileId = GlcdConfigService.getAppConfig().getActiveProfileId();
        return defaultProfileId == profile.getId();
    }

    public boolean exists(String profileName) {
        return find(Predicates.byName(profileName)) != null;
    }

    public boolean exists(GlcdConfigEmulatorProfile profile) {
        return find(Predicates.byId(profile.getId())) != null;
    }

    public GlcdConfigEmulatorProfile find(Predicate<GlcdConfigEmulatorProfile> predicate) {
        return getProfiles().stream().filter(predicate).findFirst().orElse(null);
    }

    public List<GlcdConfigEmulatorProfile> findList(Predicate<GlcdConfigEmulatorProfile> predicate) {
        return getProfiles().filtered(predicate);
    }

    public GlcdConfigEmulatorProfile create(String newName) {
        return create(newName, this.activeProfile);
    }

    public GlcdConfigEmulatorProfile create(String newName, GlcdConfigEmulatorProfile profile) {
        GlcdConfigEmulatorProfile newProfile = new GlcdConfigEmulatorProfile(profile);
        if (exists(newProfile))
            log.error("Profile Id already exists: {}", newProfile.getId());
        if (!StringUtils.isBlank(newName))
            newProfile.setName(newName);
        else
            newProfile.setName(createDefaultDuplicateName(newProfile));
        if (profiles.add(newProfile)) {
            return newProfile;
        }
        return null;
    }

    public void delete(GlcdConfigEmulatorProfile profile) {
        profiles.remove(profile);
    }

    public void save(GlcdConfigEmulatorProfile profile) {
        try {
            if (profile.getFile() == null)
                profile.setFile(new File(createProfilePath(profile)));
            GlcdConfigService.save(profile, profile.getFile());
        } catch (IOException e) {
            log.error("Unable to save active profile", e);
        }
    }

    public void save() {
        try {
            isModified();
            GlcdConfigService.save(activeProfile, createProfilePath(activeProfile));
        } catch (IOException e) {
            log.error("Unable to save active profile", e);
        }
    }

    public void update(GlcdConfigEmulatorProfile newCopy) {
        if (newCopy == null)
            throw new NullPointerException("Profile cannot be null");
        GlcdConfigEmulatorProfile oldCopy = getProfiles().get(newCopy.getId());
        try {
            if (!BeanUtils.deepEquals(oldCopy, newCopy)) {
                org.apache.commons.beanutils.BeanUtils.copyProperties(oldCopy, newCopy);
                oldCopy.setLastUpdated(ZonedDateTime.now());
                log.debug("[PROFILE UPDATE] : Id = {}", newCopy.getId());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to update profile properties", e);
        }
    }

    public void refresh() {
        String profileDirPath = GlcdConfigService.getAppConfig().getProfileDirPath();
        FileUtils.ensureDirectoryExistence(profileDirPath);
        profiles.clear();
        profiles.addAll(getProfilesFromFS());
    }

    public ObservableList<GlcdConfigEmulatorProfile> getProfiles() {
        if (profiles.isEmpty())
            refresh();
        return profiles;
    }

    public boolean isModified() {
        int totalChanges = getUncommittedChanges().size();
        log.debug("TOTAL UNCOMMITTED CHANGES: {}", totalChanges);
        return totalChanges > 0;
    }

    public boolean isModified(GlcdConfigEmulatorProfile newCopy) {
        GlcdConfigEmulatorProfile oldCopy = getProfileFromFS(newCopy);
        return !BeanUtils.deepEquals(oldCopy, newCopy);
    }

    public List<GlcdConfigEmulatorProfile> getUncommittedChanges() {
        List<GlcdConfigEmulatorProfile> changes = new ArrayList<>();
        List<GlcdConfigEmulatorProfile> oldProfiles = getProfilesFromFS();
        for (GlcdConfigEmulatorProfile profile : profiles) {
            //New profile
            if (profile.getFile() == null || !oldProfiles.contains(profile)) {
                changes.add(profile);
                continue;
            }
            //Updated profile
            GlcdConfigEmulatorProfile oldCopy = oldProfiles.stream().filter(p -> p.equals(profile)).findFirst().orElse(null);
            if (oldCopy != null && !BeanUtils.deepEquals(oldCopy,  profile)) {
                log.debug("Modified Profile: {}", profile);
                changes.add(profile);
            }
        }
        return changes;
    }

    public List<GlcdConfigEmulatorProfile> getModifiedProfiles() {
        List<GlcdConfigEmulatorProfile> savedProfiles = getProfilesFromFS();
        return null;
    }

    public List<GlcdConfigEmulatorProfile> getProfilesFromFS() {
        log.debug("[START] LOAD PROFILES FROM FS");
        ArrayList<GlcdConfigEmulatorProfile> profiles = new ArrayList<>();
        try {
            String profileDirPath = GlcdConfigService.getAppConfig().getProfileDirPath();
            Files.list(new File(profileDirPath).toPath()).forEach(path -> {
                GlcdConfigEmulatorProfile profile = getProfileFromFS(path.toFile());
                profiles.add(profile);
            });
        } catch (IOException e) {
            log.error("An error occured while retrieving the profiles", e);
        }
        log.debug("[END] LOAD PROFILES FROM FS");
        return profiles;
    }

    public GlcdConfigEmulatorProfile getProfile(int profileId) {
        return getProfiles().stream().filter(p -> p.getId() == profileId).findFirst().orElse(null);
    }

    public GlcdConfigEmulatorProfile getProfileFromFS(GlcdConfigEmulatorProfile profile) {
        return getProfileFromFS(profile.getFile());
    }

    public GlcdConfigEmulatorProfile getProfileFromFS(File profile) {
        if (profile == null)
            throw new NullPointerException("File cannot be null");
        GlcdConfigEmulatorProfile profileFromFs = GlcdConfigService.getConfig(profile, GlcdConfigEmulatorProfile.class);
        log.debug("[GET PROFILE FROM FS]: Name = {}", profileFromFs);
        return profileFromFs;
    }

    public GlcdConfigEmulatorProfile getProfileFromFS(int profileId) {
        GlcdConfigEmulatorProfile profile = getProfilesFromFS().stream().filter(p -> p.getId() == profileId).findFirst().orElse(null);
        log.debug("[GET PROFILE FROM FS BY ID]: Profile Id = {}", profileId);
        return profile;
    }

    public GlcdConfigEmulatorProfile getActiveProfile() {
        if (activeProfile == null) {
            int activeProfileId = GlcdConfigService.getAppConfig().getActiveProfileId();
            activeProfile = getProfileFromFS(activeProfileId);
            //If no profile has been found with the specified ID, then select the first entry
            if (activeProfile == null && getProfiles().size() > 0) {
                log.debug("[GET ACTIVE PROFILE]: No default profile found. Selecting first entry");
                activeProfile = getProfiles().get(0);
            }
        }
        log.debug("Retrieving active profile from cache");
        return activeProfile;
    }

    public void setActiveProfile(GlcdConfigEmulatorProfile activeProfile) {
        this.activeProfile = activeProfile;
    }

    public String createDefaultDuplicateName(GlcdConfigEmulatorProfile profile) {
        if (!exists(profile))
            return profile.getName();
        int nameCounter = 1;
        String newName = StringUtils.capitalize(profile.getName()) + " (" + nameCounter + ")";
        while (exists(newName))
            newName = StringUtils.capitalize(profile.getName()) + " (" + nameCounter++ + ")";
        return newName;
    }


    private String createDefaultProfileName(GlcdConfigEmulatorProfile profile) {
        if (profile == null)
            throw new NullPointerException("Profile cannot be null");
        return "glcd_" +
                profile.getDisplaySizeWidth() +
                "x" +
                profile.getDisplaySizeHeight() +
                "_" +
                (int) profile.getPixelSize() +
                "px";
    }

    private String sanitizeProfileName(String name) {
        if (StringUtils.isBlank(name))
            return createDefaultProfileName(activeProfile);
        return name.replaceAll("\\s", "_").toLowerCase();
    }

    private String createProfilePath(GlcdConfigEmulatorProfile profile) {
        if (profile == null)
            throw new NullPointerException("Profile argument must not be null");
        return createProfilePath(profile.getName());
    }

    private String createProfilePath(String name) {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("Profile name must not be empty");
        String dirPath = GlcdConfigService.getAppConfig().getProfileDirPath();
        return dirPath + File.separator + PROFILE_PREFIX + name.toLowerCase() + PROFILE_SUFFIX;
    }
}
