package com.filesharingapp.security;

import com.filesharingapp.utils.AppConfig;
import com.filesharingapp.utils.LoggerUtil;

/**
 * AuthUtil
 * --------
 * Very simple token-based protection for API calls.
 */
public final class AuthUtil {

    private static final String EXPECTED_TOKEN =
            AppConfig.get("security.api.token", "changeme-token");

    private AuthUtil() {
    }

    /** Check if a provided token matches expected one. */
    public static boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            LoggerUtil.warn("Missing auth token");
            return false;
        }
        boolean ok = EXPECTED_TOKEN.equals(token.trim());
        if (!ok) {
            LoggerUtil.warn("Invalid auth token");
        }
        return ok;
    }
}
