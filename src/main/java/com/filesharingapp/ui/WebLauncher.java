package com.filesharingapp.ui;

import com.filesharingapp.controller.MainController;
import com.filesharingapp.utils.LoggerUtil;

/**
 * WebLauncher
 * --------------------
 * Entry point to start the FileSharingApp web UI.
 * Launches the embedded HTTP server and auto-opens the browser.
 */
public class WebLauncher {

    public static void main(String[] args) {
        LoggerUtil.info("📸 Welcome to the Secure Photo Station!");
        LoggerUtil.info("Starting File Sharing App...");
        new MainController().start();
    }

    /**
     * Opens a browser tab at the given URL.
     * Called by MainController after server startup.
     */
    public static void openUi(String url) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                System.out.println("🌐 Opened browser at " + url);
            } else {
                System.out.println("Please open your browser manually at: " + url);
            }
        } catch (Exception e) {
            System.err.println("Failed to open UI: " + e.getMessage());
        }
    }
}
