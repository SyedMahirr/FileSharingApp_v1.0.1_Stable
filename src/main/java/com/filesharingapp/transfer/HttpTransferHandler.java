package com.filesharingapp.transfer;

import com.filesharingapp.utils.LoggerUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HttpTransferHandler
 * -------------------
 * Implements file upload/download via HTTP.
 * Works with /api/send-http endpoint of FileSharingServer.
 */
public class HttpTransferHandler implements TransferMethod {

    @Override
    public void send(String senderName, File file, String method, int port, String targetHost) throws Exception {
        LoggerUtil.info("[HTTP] Preparing to upload file...");

        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File does not exist: " + file);
        }

        String urlStr = "http://" + targetHost + ":" + port + "/api/send-http";
        LoggerUtil.info("[HTTP] Target URL: " + urlStr);

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setRequestProperty("name", senderName);
        conn.setRequestProperty("role", "Sender");
        conn.setRequestProperty("method", "HTTP");

        try (OutputStream os = conn.getOutputStream();
             FileInputStream fis = new FileInputStream(file)) {
            fis.transferTo(os);
        }

        int code = conn.getResponseCode();
        LoggerUtil.info("[HTTP] Response code: " + code);
        conn.disconnect();
    }

    @Override
    public void receive(String savePath) throws Exception {
        // Receiver side handled by FileSharingServer /api/send-http
        LoggerUtil.info("[HTTP] Receiver handled by FileSharingServer.");
    }
}
