package com.filesharingapp.tests;

import com.filesharingapp.controller.MainController;
import org.testng.annotations.Test;

/**
 * LaunchServerTest
 * ----------------
 * Runs once to start the server + open Web UI.
 */
public class LaunchServerTest {

    @Test
    public void startServerAndUi() {
        new MainController().start();
        // Keep test alive briefly to ensure server is up.
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException ignored) {
        }
    }
}
