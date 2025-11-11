package com.filesharingapp.transfer;

import com.filesharingapp.utils.LoggerUtil;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

/**
 * S3TransferHandler
 * -----------------
 * Demonstration of AWS S3-based transfer.
 * Assumes valid AWS credentials in environment or ~/.aws/credentials.
 */
public class S3TransferHandler implements TransferMethod {

    @Override
    public void send(String senderName, File file, String method, int port, String targetHost) throws Exception {
        LoggerUtil.info("[S3] Uploading file to S3 bucket...");

        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File not found: " + file);
        }

        try (S3Client s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()) {

            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(targetHost)
                    .key(file.getName())
                    .build();

            s3.putObject(req, RequestBody.fromFile(file));
            LoggerUtil.success("[S3] Uploaded " + file.getName() + " to bucket: " + targetHost);
        }
    }

    @Override
    public void receive(String savePath) {
        LoggerUtil.info("[S3] Receiving not implemented (use AWS CLI or SDK).");
    }
}
