package com.filesharingapp.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * ActivityLogger
 * --------------
 * Writes a simple CSV line for each transfer.
 */
public final class ActivityLogger {

    private static final Path LOG_DIR = Path.of("logs");
    private static final Path AUDIT_FILE = LOG_DIR.resolve("transfer_audit.csv");

    static {
        try {
            Files.createDirectories(LOG_DIR);
            if (Files.notExists(AUDIT_FILE)) {
                try (Writer w = new FileWriter(AUDIT_FILE.toFile(), true)) {
                    w.write("timestamp,mode,senderIP,receiverIP,fileName,bytes,durationMs,status,message\n");
                }
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to init ActivityLogger", e);
        }
    }

    private ActivityLogger() {
    }

    public static void logTransfer(String mode,
                                   String senderIp,
                                   String receiverIp,
                                   String fileName,
                                   long bytes,
                                   long durationMs,
                                   String status,
                                   String message) {
        try (Writer w = new FileWriter(AUDIT_FILE.toFile(), true)) {
            w.write(String.join(",",
                    LocalDateTime.now().toString(),
                    safe(mode),
                    safe(senderIp),
                    safe(receiverIp),
                    safe(fileName),
                    String.valueOf(bytes),
                    String.valueOf(durationMs),
                    safe(status),
                    safe(message))
                    + "\n");
        } catch (IOException e) {
            LoggerUtil.error("Failed to write transfer audit", e);
        }
    }

    private static String safe(String v) {
        if (v == null) return "";
        return v.replace(",", "_").replace("\n", " ").trim();
    }
}
