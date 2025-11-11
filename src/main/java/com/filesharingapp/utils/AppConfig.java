package com.filesharingapp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * AppConfig
 * ---------
 * Loads values from application.properties once.
 * Everyone else reads through this helper.
 */
public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        // Load file from classpath.
        try (InputStream in = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in != null) {
                PROPS.load(in);
                LoggerUtil.info("Loaded application.properties");
            } else {
                LoggerUtil.warn("application.properties not found, using defaults");
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to load application.properties", e);
        }
    }

    private AppConfig() {
    }

    /** Read config or return default value. */
    public static String get(String key, String def) {
        String v = PROPS.getProperty(key);
        return (v == null || v.isBlank()) ? def : v.trim();
    }

    /** Read integer with default and simple error handling. */
    public static int getInt(String key, int def) {
        try {
            return Integer.parseInt(get(key, String.valueOf(def)));
        } catch (NumberFormatException e) {
            LoggerUtil.warn("Invalid int for key " + key + ", using default " + def);
            return def;
        }
    }

    /** Read boolean with default. */
    public static boolean getBoolean(String key, boolean def) {
        String v = get(key, String.valueOf(def));
        return "true".equalsIgnoreCase(v) || ("false".equalsIgnoreCase(v) ? false : def);
    }
}
