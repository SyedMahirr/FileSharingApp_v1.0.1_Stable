package com.filesharingapp.security;

import com.filesharingapp.utils.AppConfig;
import com.filesharingapp.utils.LoggerUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AesUtil
 * -------
 * Optional AES helper.
 * If app.security.aes.enabled=false in application.properties,
 * methods just return original bytes.
 *
 * This is deliberately simple and not meant as full production crypto.
 */
public final class AesUtil {

    private static final boolean ENABLED =
            AppConfig.getBoolean("app.security.aes.enabled", false);

    // 16-byte demo key from config (never hardcode real secrets).
    private static final String KEY =
            AppConfig.get("app.security.aes.key", "0123456789abcdef");

    private static final String ALGO = "AES";

    private AesUtil() {
    }

    /** Encrypt data or return original if disabled. */
    public static byte[] encrypt(byte[] data) {
        if (!ENABLED) return data; // AES off.
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            LoggerUtil.error("AES encrypt failed, sending plain", e);
            return data;
        }
    }

    /** Decrypt data or return original if disabled. */
    public static byte[] decrypt(byte[] data) {
        if (!ENABLED) return data;
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), ALGO);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            LoggerUtil.error("AES decrypt failed, using raw bytes", e);
            return data;
        }
    }
}
