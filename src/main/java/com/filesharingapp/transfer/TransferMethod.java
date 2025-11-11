package com.filesharingapp.transfer;

import java.io.File;

/**
 * TransferMethod
 * ---------------
 * Common interface for all transfer types (HTTP, ZeroTier, S3).
 * Each implementation must provide matching send() and receive() methods.
 */
public interface TransferMethod {

    /**
     * Send a file to a remote destination.
     *
     * @param senderName Name of the sender (for logs).
     * @param file File object to be transferred.
     * @param method Name of the transfer method (HTTP, ZeroTier, S3).
     * @param port Port number to use for the connection.
     * @param targetHost Target host address or identifier.
     * @throws Exception If any I/O or validation error occurs.
     */
    void send(String senderName, File file, String method, int port, String targetHost) throws Exception;

    /**
     * Receive files and store them in a local directory.
     *
     * @param savePath Directory where received files will be saved.
     * @throws Exception If validation or write errors occur.
     */
    void receive(String savePath) throws Exception;
}
