package com.filesharingapp.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * DuplicateChecker
 * ----------------
 * Very small helper that tracks files that were already seen.
 * This is not cryptographically strong. It is used only for UX.
 */
public final class DuplicateChecker {

    private static final Path LOG_DIR = Path.of("logs");
    private static final Path DUP_FILE = LOG_DIR.resolve("duplicate_files.csv");
    private static final Set<String> seen = new HashSet<>();

    static {
        try {
            Files.createDirectories(LOG_DIR);
            if (Files.exists(DUP_FILE)) {
                try (BufferedReader br = Files.newBufferedReader(DUP_FILE)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        seen.add(line.trim());
                    }
                }
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to init DuplicateChecker", e);
        }
    }

    private DuplicateChecker() {
    }

    /** Record and return true if this combination was already used. */
    public static boolean isDuplicate(String sender, String receiver, String fileName) {
        String key = sender + "|" + receiver + "|" + fileName;
        if (seen.contains(key)) {
            return true;
        }
        seen.add(key);
        try (Writer w = new FileWriter(DUP_FILE.toFile(), true)) {
            w.write(key + "\n");
        } catch (IOException e) {
            LoggerUtil.error("Failed to log duplicate", e);
        }
        return false;
    }
}
