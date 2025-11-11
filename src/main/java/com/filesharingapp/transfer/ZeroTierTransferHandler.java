package com.filesharingapp.transfer;

import com.filesharingapp.utils.LoggerUtil;
import java.io.File;
import java.io.IOException;

/**
 * ZeroTierTransferHandler
 * -----------------------
 * Mock implementation that simulates file sending and receiving
 * using the ZeroTier network for demonstration purposes.
 */
public class ZeroTierTransferHandler implements TransferMethod {

    @Override
    public void send(String senderName, File file, String method, int port, String targetHost) throws Exception {
        LoggerUtil.info("[ZeroTier] Preparing to send file via ZeroTier...");

        if (senderName == null || senderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender name cannot be empty.");
        }
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IOException("File not found: " + (file != null ? file.getPath() : "null"));
        }
        if (targetHost == null || targetHost.trim().isEmpty()) {
            throw new IllegalArgumentException("Target host cannot be empty.");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }

        LoggerUtil.info("[ZeroTier] Connecting to peer node: " + targetHost + " on port " + port);
        LoggerUtil.info("[ZeroTier] Sending file: " + file.getName() + " (" + file.length() + " bytes)");
        Thread.sleep(1500);
        LoggerUtil.success("[ZeroTier] File sent successfully ✅");
    }

    @Override
    public void receive(String savePath) throws Exception {
        LoggerUtil.info("[ZeroTier] Listening for incoming transfer...");

        if (savePath == null || savePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination path cannot be empty.");
        }

        File destination = new File(savePath);
        if (!destination.exists() && !destination.mkdirs()) {
            throw new IOException("Unable to create destination folder: " + savePath);
        }

        LoggerUtil.info("[ZeroTier] Connected to peer. Receiving file...");
        Thread.sleep(1500);
        LoggerUtil.success("[ZeroTier] File received and saved to: " + destination.getAbsolutePath());
    }
}
