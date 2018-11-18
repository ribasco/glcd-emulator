/*-
 * ========================START=================================
 * Organization: Rafael Ibasco
 * Project: GLCD Emulator
 * Filename: Common.java
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
package com.ibasco.glcdemulator.constants;

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

    public static final String PROJECT_URL = "https://github.com/ribasco/glcd-emulator";

    public static final String DONATE_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=WQBFBY76JUN7Y";

    public static final String RELEASE_URL = "https://github.com/ribasco/glcd-emulator/releases";

    public static final String REPORT_ISSUE_URL = "https://github.com/ribasco/glcd-emulator/issues/new";

    public static final String DEVELOPER_EMAIL = "ribasco@gmail.com";
}
