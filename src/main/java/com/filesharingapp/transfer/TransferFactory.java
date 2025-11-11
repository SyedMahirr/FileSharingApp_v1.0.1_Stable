package com.filesharingapp.transfer;

/**
 * TransferFactory
 * ---------------
 * Factory for selecting correct transfer handler at runtime.
 */
public class TransferFactory {

    public static TransferMethod getHandler(String method) {
        if (method == null) return null;
        return switch (method.trim().toUpperCase()) {
            case "HTTP" -> new HttpTransferHandler();
            case "ZEROTIER" -> new ZeroTierTransferHandler();
            case "S3" -> new S3TransferHandler();
            default -> null;
        };
    }
}
