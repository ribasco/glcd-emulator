package com.ibasco.glcdemu.utils;

import com.ibasco.glcdemu.annotations.NoCompare;
import com.ibasco.glcdemu.beans.GlcdConfigEmulatorProfile;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanUtils.class);

    public static boolean deepEquals(Object oldObject, Object newObject) {
        log.debug("[START] Bean Comparison: Old = {}, New = {}", oldObject, newObject);

        if (oldObject == null || newObject == null)
            return oldObject == newObject;

        if (!oldObject.getClass().equals(newObject.getClass()))
            return false;

        BeanMap map = new BeanMap(oldObject);

        PropertyUtilsBean propUtils = new PropertyUtilsBean();

        Class<?> argType = oldObject.getClass();

        List<Field> excludedProperties = FieldUtils.getFieldsListWithAnnotation(argType, NoCompare.class);

        try {
            for (Object propNameObject : map.keySet()) {
                String propertyName = (String) propNameObject;
                Object oldValue = propUtils.getProperty(oldObject, propertyName);
                Object newValue = propUtils.getProperty(newObject, propertyName);
                boolean isEquals = (oldValue == null || newValue == null) ? oldValue == newValue : oldValue.equals(newValue);
                //Skip excluded properties
                if ("class".equals(propertyName) || excludedProperties.stream().anyMatch(f -> f.getName().equals(propertyName))) {
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
            throw new RuntimeException("Error occured during comparison of two beans for equality", e);
        } finally {
            log.debug("[END] Bean Comparison: Old = {}, New = {}", oldObject, newObject);
        }
        return true;
    }

    public static void main(String[] args) {
        GlcdConfigEmulatorProfile profileOrig = new GlcdConfigEmulatorProfile();
        profileOrig.setName("OldProfile");
        GlcdConfigEmulatorProfile profileNew = new GlcdConfigEmulatorProfile();
        profileNew.setName("NewProfile");
        log.info("Equals = {}", BeanUtils.deepEquals(profileOrig, profileNew));
    }
}
