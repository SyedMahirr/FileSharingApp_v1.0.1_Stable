package com.filesharingapp.utils;

/**
 * RetryUtil
 * ---------
 * Runs an action several times with sleep between attempts.
 * Used for network calls so we can auto-recover from glitches.
 */
public final class RetryUtil {

    private RetryUtil() {
    }

    /**
     * Run the given action with retry.
     *
     * @param action     the work to run (e.g. send one chunk)
     * @param maxRetries how many times total
     * @param delayMs    delay between tries
     * @return true on success, false if all attempts failed
     */
    public static boolean runWithRetry(Runnable action, int maxRetries, long delayMs) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                action.run();
                return true;
            } catch (Exception e) {
                attempt++;
                LoggerUtil.warn("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt >= maxRetries) {
                    LoggerUtil.error("All retry attempts failed", e);
                    return false;
                }
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    LoggerUtil.error("Retry sleep interrupted", ie);
                    return false;
                }
            }
        }
        return false;
    }
}
