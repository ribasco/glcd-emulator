package com.ibasco.glcdemu.constants;

import java.io.File;

public class Common {
    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String APP_CONFIG_FILE = "settings.json";

    public static final String APP_CONFIG_PATH = USER_DIR + File.separator + APP_CONFIG_FILE;

    public static final String CACHE_DIR_PATH = USER_DIR + File.separator + ".cache";

    public static final String FONT_CACHE_DIR_PATH = CACHE_DIR_PATH + File.separator + "fonts";

    public static final String FONT_CACHE_FILE_PATH = FONT_CACHE_DIR_PATH + File.separator + "cache.json";

    public static final String THEME_DEFAULT = "menuThemeDefault";

    public static final String THEME_DEFAULT_DARK = "menuThemeDark";
}
