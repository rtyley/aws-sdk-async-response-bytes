package com.madgag.aws.sdk.async.responsebytes;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.concurrent.ExecutionException;

import static com.google.common.base.Verify.verify;
import static java.time.Duration.ofSeconds;
import static software.amazon.awssdk.regions.Region.US_EAST_1;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (S3AsyncClient s3Client = buildS3Client()) {
            System.out.println("About to fetch file...");
            KnownS3Object knownS3Object = KnownS3Object.BIG;
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObject(
                    GetObjectRequest.builder().bucket(knownS3Object.bucket()).key(knownS3Object.key()).build(),
                    AsyncResponseTransformer.toBytes()
            ).get();
            System.out.println(responseBytes.response().contentLength());
            HashCode hashOfDownloadedData = Hashing.sha256().hashBytes(responseBytes.asByteBuffer());
            verify(hashOfDownloadedData.equals(knownS3Object.sha256()),
                    "Downloaded hash (%s) doesn't match expected hash (%s)", hashOfDownloadedData, knownS3Object.sha256());
        }
    }

    private static S3AsyncClient buildS3Client() {
        return S3AsyncClient.builder()
                .region(US_EAST_1)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .httpClientBuilder(AwsCrtAsyncHttpClient
                        .builder()
                        .connectionTimeout(ofSeconds(3))
                        .maxConcurrency(100)).build();
    }

}