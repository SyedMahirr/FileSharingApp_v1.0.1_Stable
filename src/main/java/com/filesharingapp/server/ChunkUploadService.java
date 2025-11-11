package com.filesharingapp.server;

import com.filesharingapp.security.AesUtil;
import com.filesharingapp.utils.LoggerUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ChunkUploadService
 * ------------------
 * Handles chunk storage and merge for /api/send-http endpoint.
 */
public final class ChunkUploadService {

    private static final Path TMP_DIR = Path.of("tmp", "uploads");
    private static final Path RECEIVED_DIR = Path.of("received");

    static {
        try {
            Files.createDirectories(TMP_DIR);
            Files.createDirectories(RECEIVED_DIR);
        } catch (IOException e) {
            LoggerUtil.error("Failed to init upload directories", e);
        }
    }

    private ChunkUploadService() {
    }

    /**
     * Save one chunk and merge if all done.
     *
     * @return message for client
     */
    public static String handleChunk(String transferId,
                                     String fileName,
                                     int chunkIndex,
                                     long totalBytes,
                                     byte[] body) throws IOException {

        if (transferId == null || transferId.isBlank()) {
            throw new IOException("Missing transferId");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IOException("Missing fileName");
        }

        // Decrypt if AES enabled.
        byte[] plain = AesUtil.decrypt(body);

        // Save chunk.
        Path chunkFile = TMP_DIR.resolve(transferId + "." + chunkIndex + ".chunk");
        Files.write(chunkFile, plain);
        LoggerUtil.info("Stored chunk " + chunkIndex + " for " + transferId);

        // Try merge if size reached or last chunk heuristics:
        // In this simple version we merge when sum of chunk sizes >= totalBytes.
        long sum = Files.list(TMP_DIR)
                .filter(p -> p.getFileName().toString().startsWith(transferId + "."))
                .mapToLong(p -> {
                    try {
                        return Files.size(p);
                    } catch (IOException e) {
                        return 0L;
                    }
                })
                .sum();

        if (totalBytes > 0 && sum >= totalBytes) {
            Path out = RECEIVED_DIR.resolve(fileName);
            try (OutputStream outStream = Files.newOutputStream(out)) {
                int idx = 0;
                while (true) {
                    Path cf = TMP_DIR.resolve(transferId + "." + idx + ".chunk");
                    if (!Files.exists(cf)) break;
                    Files.copy(cf, outStream);
                    idx++;
                }
            }

            // Cleanup temp chunks.
            Files.list(TMP_DIR)
                    .filter(p -> p.getFileName().toString().startsWith(transferId + "."))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            LoggerUtil.warn("Failed to delete chunk " + p);
                        }
                    });

            LoggerUtil.info("Merged all chunks for " + transferId + " into " + out);
            return "MERGED";
        }

        return "CHUNK-STORED";
    }
}
