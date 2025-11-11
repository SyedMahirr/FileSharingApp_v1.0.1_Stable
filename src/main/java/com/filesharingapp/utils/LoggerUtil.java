package com.filesharingapp.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Unified Logger wrapper using Log4j2 + console mirror.
 */
public final class LoggerUtil {
    private static final Logger logger = LogManager.getLogger("FileSharingApp");

    private LoggerUtil() {}

    public static void info(String msg) {
        logger.info(msg);
        System.out.println("[INFO] " + msg);
    }

    public static void success(String msg) {
        logger.info("[SUCCESS] " + msg);
        System.out.println("[SUCCESS] " + msg);
    }

    public static void warn(String msg) {
        logger.warn(msg);
        System.out.println("[WARN] " + msg);
    }

    public static void error(String msg) {
        logger.error(msg);
        System.err.println("[ERROR] " + msg);
    }

    public static void error(String msg, Throwable t) {
        logger.error(msg, t);
        System.err.println("[ERROR] " + msg);
        if (t != null) t.printStackTrace(System.err);
    }

    /**
     * Display a prompt message on the Web UI (if running with the web frontend).
     * Falls back to console if no browser context is active.
     */
    public static void uiPrompt(String message) {
        try {
            // If the Web UI is active (FileSharingServer already started)
            System.out.println("[UI PROMPT] " + message);

            // Optional: also push to browser via a simple endpoint or WebSocket
            // This lightweight POST will not break if the UI isn’t listening.
            try {
                var url = new java.net.URL("http://localhost:8080/prompt?msg=" + java.net.URLEncoder.encode(message, "UTF-8"));
                var conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(500);
                conn.getResponseCode(); // trigger
                conn.disconnect();
            } catch (Exception ignored) { }

        } catch (Exception e) {
            System.out.println("[PromptManager fallback] " + message);
        }
    }

}
