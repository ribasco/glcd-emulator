/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: glcd-emulator
 * Filename: BeanUtils.java
 *
 * ---------------------------------------------------------
 * %%
 * Copyright (C) 2018 Rafael Ibasco
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
package com.ibasco.glcdemu.utils;

import com.ibasco.glcdemu.annotations.Auditable;
import com.ibasco.glcdemu.model.GlcdEmulatorProfile;
import javafx.scene.paint.Color;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    public static void copyProperties(Object src, Object dest) {
        try {
            org.apache.commons.beanutils.BeanUtils.copyProperties(dest, src);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to copy bean properties", e);
        }
    }

    public static List<PropertyDescriptor> diff(Object lhs, Object rhs) {
        if (lhs == null || rhs == null)
            throw new NullPointerException("Arguments must not be null");

        if (!lhs.getClass().equals(rhs.getClass()))
            throw new IllegalArgumentException("Beans should be of the same type");

        List<PropertyDescriptor> diffProps = new ArrayList<>();

        BeanMap map = new BeanMap(lhs);
        PropertyUtilsBean propUtils = new PropertyUtilsBean();
        Class<?> argType = lhs.getClass();

        List<Field> nonAuditableProperties = FieldUtils.getFieldsListWithAnnotation(argType, Auditable.class);

        try {
            for (Object propNameObject : map.keySet()) {
                String propertyName = (String) propNameObject;
                Object oldValue = propUtils.getProperty(lhs, propertyName);
                Object newValue = propUtils.getProperty(rhs, propertyName);
                PropertyDescriptor desc = propUtils.getPropertyDescriptor(lhs, propertyName);
                Method readMethod = map.getReadMethod(propertyName);

                //Skip excluded properties
                if ("class".equals(propertyName) || nonAuditableProperties.stream().anyMatch(f -> f.getName().equals(propertyName) && !f.getAnnotation(Auditable.class).enabled())) {
                    continue;
                } else if (readMethod != null && readMethod.isAnnotationPresent(Auditable.class) && !readMethod.getAnnotation(Auditable.class).enabled()) {
                    continue;
                }

                if ((oldValue == null || newValue == null) && oldValue != newValue)  {
                    diffProps.add(desc);
                    continue;
                } else if (oldValue == null) {
                    continue;
                }

                if (desc.getPropertyType().equals(Color.class)) {
                    String oldColor = NodeUtil.toHexString((Color) oldValue);
                    String newColor = NodeUtil.toHexString((Color) newValue);
                    if (!oldColor.equalsIgnoreCase(newColor))
                        diffProps.add(desc);
                } else   {
                    if (!oldValue.equals(newValue))
                        diffProps.add(desc);
                }
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("Error while getting bean diff", e);
        }

        return diffProps;
    }

    public static boolean deepEquals(Object oldObject, Object newObject) {
        log.debug("[START] Bean Comparison: Old = {}, New = {}", oldObject, newObject);

        if (oldObject == null || newObject == null)
            return oldObject == newObject;

        if (!oldObject.getClass().equals(newObject.getClass()))
            return false;

        BeanMap map = new BeanMap(oldObject);

        PropertyUtilsBean propUtils = new PropertyUtilsBean();

        Class<?> argType = oldObject.getClass();

        List<Field> excludedProperties = FieldUtils.getFieldsListWithAnnotation(argType, Auditable.class);

        try {
            for (Object propNameObject : map.keySet()) {
                String propertyName = (String) propNameObject;
                Object oldValue = propUtils.getProperty(oldObject, propertyName);
                Object newValue = propUtils.getProperty(newObject, propertyName);
                boolean isEquals = (oldValue == null || newValue == null) ? oldValue == newValue : oldValue.equals(newValue);

                //Skip excluded properties
                if ("class".equals(propertyName) || excludedProperties.stream().anyMatch(f -> f.getName().equals(propertyName) && !f.getAnnotation(Auditable.class).enabled())) {
                    log.debug("\t[EXCLUDED] Property = {}", propertyName);
                    continue;
                }
                if (!isEquals) {
                    log.debug("[NOT EQUALS]: Property = {}, Old Value = {}, New Value = {}", propertyName, oldValue, newValue);

                    return false;
                }
                log.debug("\t[EQUALS] Property = {}, Old Value = {}, New Value = {}", propertyName, oldValue, newValue);
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Error occured during comparison of two domain for equality", e);
        } finally {
            log.debug("[END] Bean Comparison: Old = {}, New = {}", oldObject, newObject);
        }
        return true;
    }

    public static void main(String[] args) {
        GlcdEmulatorProfile profileOrig = new GlcdEmulatorProfile();
        //profileOrig.setName("OldProfile");
        GlcdEmulatorProfile profileNew = new GlcdEmulatorProfile();
        //profileNew.setName("NewProfile");

        try {
            for (PropertyDescriptor desc : BeanUtils.diff(profileOrig, profileNew)) {
                log.debug("- Name: {}, Value = {}", desc.getName(), PropertyUtils.getProperty(profileNew, desc.getName()));
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
