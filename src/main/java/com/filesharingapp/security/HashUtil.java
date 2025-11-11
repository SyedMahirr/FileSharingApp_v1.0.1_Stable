package com.filesharingapp.security;

import java.security.MessageDigest;

/**
 * HashUtil
 * --------
 * Used to compute checksum for integrity checks.
 */
public final class HashUtil {

    private HashUtil() {
    }

    /** Compute SHA-256 hex string for given bytes. */
    public static String sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            // If this fails something is very wrong; we rethrow as runtime.
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
