package com.filesharingapp.utils;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * ZipUtil
 * -------
 * Helper to produce a single-file ZIP for transfer.
 * Does NOT change or remove any existing behavior;
 * callers opt-in by using zipIfNeeded(..).
 */
public final class ZipUtil {

    private ZipUtil() {
    }

    /**
     * If the given file is already a .zip, return it as-is.
     * Otherwise create {name}.zip in the same folder and return that.
     */
    public static File zipIfNeeded(File file) throws IOException {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".zip")) {
            return file;
        }

        File zipFile = new File(file.getParentFile(),
                file.getName() + ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry entry = new ZipEntry(file.getName());
            zos.putNextEntry(entry);
            Files.copy(file.toPath(), zos);
            zos.closeEntry();
        }

        LoggerUtil.info("[Zip] Created archive for transfer: " + zipFile.getName());
        return zipFile;
    }
}
