package com.ibasco.glcdemu.constants;

import java.io.File;

public class Common {
    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String APP_CONFIG_FILE = "settings.json";

    public static final String APP_CONFIG_PATH = USER_DIR + File.separator + APP_CONFIG_FILE;
}
