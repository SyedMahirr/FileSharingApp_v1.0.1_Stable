package com.filesharingapp.controller;

import com.filesharingapp.server.FileSharingServer;
import com.filesharingapp.ui.WebLauncher;
import com.filesharingapp.utils.LoggerUtil;

import java.util.Random;
import java.util.logging.Level;

/**
 * MainController
 * -------------------
 * Central orchestrator for FileSharingApp.
 * Handles coordinated startup of the embedded HTTP server and Web UI.
 * Used by:
 *   - WebLauncher (manual mode)
 *   - LaunchServerTest (automated tests)
 *
 * This version aligns with v1.0.15_EnterpriseQA_WebUI_BDD_Stable baseline.
 */
public class MainController {

    /**
     * Start the FileSharingServer and open Web UI automatically.
     * Handles dynamic port fallback (8080–8090).
     */
    public void start() {
        int port = 8080; // Default port
        boolean started = false;
        int attempts = 0;

        // Try dynamic port allocation between 8080–8090
        while (!started && port <= 8090) {
            try {
                LoggerUtil.info("Attempting to start FileSharingServer on port " + port + "...");
                FileSharingServer server = new FileSharingServer(port);
                server.start();  // Blocking call, spawns Jetty thread
                started = true;
                LoggerUtil.info("MainController: server started successfully on port " + port);
            } catch (Exception e) {
                attempts++;
                LoggerUtil.error("Port " + port + " is busy. Trying next...");
                port++;
                if (attempts > 10) {
                    LoggerUtil.error("MainController: all ports 8080–8090 are unavailable.");
                    return;
                }
            }
        }

        if (started) {
            try {
                LoggerUtil.info("MainController: launching Web UI...");
                WebLauncher.openUi("http://localhost:" + port + "/");
            } catch (Exception e) {
                LoggerUtil.error("MainController: failed to launch browser: " + e.getMessage());
            }
        } else {
            LoggerUtil.error("MainController: server failed to start after retries.");
        }
    }
}
