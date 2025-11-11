package com.filesharingapp.security;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import com.filesharingapp.utils.LoggerUtil;

/**
 * ValidationUtil
 * --------------
 * Central place for all user input checks.
 * Keep rules here so UI + API both behave the same.
 */
public final class ValidationUtil {

    private static final long MAX_FILE_BYTES = 5L * 1024 * 1024 * 1024; // 5 GB

    private ValidationUtil() {
    }

    /** Check that display name is simple and safe. */
    public static boolean isSafeName(String name) {
        if (name == null) return false;
        String t = name.trim();
        if (t.isEmpty()) return false;
        // Letters, digits, space, dash, underscore, dot.
        boolean ok = t.matches("[A-Za-z0-9 _.-]{1,50}");
        if (!ok) {
            LoggerUtil.warn("Rejected unsafe name: " + name);
        }
        return ok;
    }

    /** Check that mode is one of allowed values. */
    public static boolean isValidMode(String mode) {
        return "HTTP".equalsIgnoreCase(mode)
                || "ZEROTIER".equalsIgnoreCase(mode)
                || "S3".equalsIgnoreCase(mode);
    }

    /** Check that path points to readable .zip file for sender. */
    public static boolean isValidZipToSend(String path) {
        if (path == null || path.isBlank()) return false;
        File f = new File(path.trim());
        if (!f.exists() || !f.isFile()) return false;
        if (!f.getName().toLowerCase().endsWith(".zip")) return false;
        if (f.length() <= 0 || f.length() > MAX_FILE_BYTES) return false;
        return true;
    }

    /** Check that folder exists or can be created for receiver. */
    public static boolean ensureFolder(String path) {
        try {
            Path p = Path.of(path);
            if (Files.exists(p)) {
                return Files.isDirectory(p);
            }
            Files.createDirectories(p);
            return true;
        } catch (Exception e) {
            LoggerUtil.error("Failed to prepare folder: " + path, e);
            return false;
        }
    }

    /** Simple IP / host placeholder validation. */
    public static boolean isHostLike(String host) {
        if (host == null || host.isBlank()) return false;
        String t = host.trim();
        // Very lenient: letters, digits, dot, dash.
        return t.matches("[A-Za-z0-9.-]{1,253}");
    }

    /** Port must be 1..65535. */
    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }
}
