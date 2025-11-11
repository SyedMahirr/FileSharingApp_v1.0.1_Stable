package com.filesharingapp.tests;

import com.filesharingapp.server.FileSharingServer;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;

/**
 * StepDefinitions
 * ----------------
 * Simple API checks for FileSharingServer to validate endpoints
 * using Cucumber BDD + TestNG assertions.
 */
public class StepDefinitions {

    private int serverPort;
    private FileSharingServer server;

    @Given("the file sharing server is running")
    public void the_server_is_running() throws Exception {
        // Pick a free port for test
        try (ServerSocket socket = new ServerSocket(0)) {
            serverPort = socket.getLocalPort();
        }

        // Start server on that port in a background thread
        server = new FileSharingServer(serverPort);
        new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        System.out.println("[Test] Server started on port: " + serverPort);
        Assert.assertTrue(serverPort > 0, "Server port must be valid.");
        Thread.sleep(1500); // give it time to boot
    }

    @Then("the health endpoint returns OK")
    public void health_returns_ok() throws Exception {
        String body = get("http://localhost:" + serverPort + "/health");
        Assert.assertTrue(body.contains("\"status\":\"OK\""), "Health endpoint failed.");
    }

    @Then("the handshake endpoint returns READY")
    public void handshake_returns_ready() throws Exception {
        String body = get("http://localhost:" + serverPort + "/handshake?name=TestReceiver");
        Assert.assertTrue(body.contains("\"receiver\":\"TestReceiver\""), "Handshake endpoint failed.");
    }

    // Utility method to perform GET requests
    private String get(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(5000);
        conn.setRequestMethod("GET");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
