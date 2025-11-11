package com.filesharingapp.utils;

public final class ValidationMessages {
    private ValidationMessages() {}
    public static final String FILE_REQUIRED   = "Please pick a ZIP file before starting.";
    public static final String FILE_TOO_LARGE  = "File is larger than the allowed limit.";
    public static final String FILE_NOT_FOUND  = "We could not find the file you selected.";
    public static final String HOST_REQUIRED   = "Please enter your partner's IP / host.";
    public static final String PORT_INVALID    = "Port must be between 1 and 65535.";
    public static final String MODE_INVALID    = "Pick HTTP, ZeroTier, or S3.";
}
