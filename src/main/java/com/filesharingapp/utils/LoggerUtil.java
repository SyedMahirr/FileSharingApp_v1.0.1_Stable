package com.filesharingapp.utils;

import java.io.IOException;
import java.util.logging.*;

/**
 * LoggerUtil
 * -------------------
 * Provides centralized logging for the entire File Sharing App.
 * Uses Java's built-in logging system (no external dependencies).
 *
 * ✅ Automatically creates logs/filesharingapp.log
 * ✅ Adds both console and file handlers
 * ✅ Offers overloads for single or exception-based messages
 */
public class LoggerUtil {

    // Create a single reusable logger for all app classes
    private static final Logger LOGGER = Logger.getLogger("FileSharingApp");

    static {
        try {
            // Ensure consistent logging format
            LogManager.getLogManager().reset();

            // Console handler setup
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(consoleHandler);

            // File handler setup
            FileHandler fileHandler = new FileHandler("logs/filesharingapp.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);

            LOGGER.setLevel(Level.ALL);

        } catch (IOException e) {
            System.err.println("Logger initialization failed: " + e.getMessage());
        }
    }

    /**
     * Logs informational messages.
     *
     * @param message The message text to log.
     */
    public static void info(String message) {
        LOGGER.log(Level.INFO, message);
    }

    /**
     * Logs warning messages (used for recoverable issues).
     *
     * @param message The message text to log.
     */
    public static void warn(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    /**
     * Logs severe or fatal errors (used for exceptions).
     *
     * @param message The error message to log.
     * @param throwable Optional exception stack trace.
     */
    public static void error(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message, throwable);
    }

    /**
     * Overloaded method to log simple error messages
     * without requiring a Throwable parameter.
     *
     * @param message The error message to log.
     */
    public static void error(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    /**
     * Logs a debug message (visible only in file logs).
     *
     * @param message The debug message.
     */
    public static void debug(String message) {
        LOGGER.log(Level.FINE, message);
    }

    /**
     * Logs a success event with a green check emoji.
     *
     * @param message The message text to log.
     */
    public static void success(String message) {
        LOGGER.log(Level.INFO, "✅ " + message);
    }
}
