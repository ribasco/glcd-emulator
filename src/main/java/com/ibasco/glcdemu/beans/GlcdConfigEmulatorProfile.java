package com.ibasco.glcdemu.beans;

import com.ibasco.glcdemu.services.GlcdConfigService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class GlcdConfigEmulatorProfile extends GlcdConfigEmulator {

    private static final Logger log = LoggerFactory.getLogger(GlcdConfigEmulatorProfile.class);

    private int id;

    private String name;

    private String description;

    public GlcdConfigEmulatorProfile() {}

    public GlcdConfigEmulatorProfile(GlcdConfigEmulatorProfile copy) {
        if (copy == null)
            return;
        try {
            BeanUtils.copyProperties(this, copy);
            id = GlcdConfigService.getAppConfig().nextId();
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to copy bean properties", e);
        }
    }

    public GlcdConfigEmulatorProfile(String name) {
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlcdConfigEmulatorProfile profile = (GlcdConfigEmulatorProfile) o;
        return id == profile.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "GlcdConfigProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
