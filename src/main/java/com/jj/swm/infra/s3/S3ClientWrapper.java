package com.jj.swm.infra.s3;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.net.URI;
import java.text.Normalizer;

@Component
public class S3ClientWrapper {

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name.public}")
    private String publicBucketName;

    @Value("${aws.s3.bucket-name.private}")
    private String privateBucketName;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-access-key}")
    private String secretAccessKey;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretAccessKey)))
                .region(Region.of(region))
                .forcePathStyle(true)
                .build();
    }

    public void putObject(File file) {
        putObject(true, file);
    }

    public void putObject(boolean isPublic, File file) {
        String bucketName = isPublic ? publicBucketName : privateBucketName;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(System.currentTimeMillis() + Normalizer.normalize(file.getName(), Normalizer.Form.NFC))
                .build();
        s3Client.putObject(putObjectRequest, file.toPath());
    }
}
