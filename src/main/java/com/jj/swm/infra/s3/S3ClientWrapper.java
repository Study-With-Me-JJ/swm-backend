package com.jj.swm.infra.s3;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.time.Duration;

@Slf4j
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
    private S3Presigner presigner;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @PostConstruct
    public void init() {
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretAccessKey)))
                .region(Region.of(region))
                .forcePathStyle(true)
                .build();

        presigner = S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretAccessKey)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .region(Region.of(region))
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

    public String getPresignedURL(boolean isPublic, String key, boolean isImage) {
        String bucketName = isPublic ? publicBucketName : privateBucketName;
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(isImage ? "image/jpeg" : "application/octet-stream")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();


        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toExternalForm();
    }

    /**
     * URL에서 이미지를 다운로드하여 S3에 업로드
     *
     * @param isPublic 업로드할 버킷이 public인지 private인지 지정
     * @param imageUrl 다운로드할 이미지 URL
     * @return 업로드된 파일의 주소
     */
    public String putImageFromUrl(boolean isPublic, String imageUrl) {
        String bucketName = isPublic ? publicBucketName : privateBucketName;

        try {
            // HTTP GET 요청 생성
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            // 요청 실행 및 응답 받기
            HttpResponse<InputStream> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

            // HTTP 응답 상태 확인
            int statusCode = httpResponse.statusCode();
            if (statusCode != 200) {
                throw new RuntimeException("Failed to download image. HTTP status code: " + statusCode);
            }

            // InputStream 데이터를 메모리로 읽기
            byte[] fileData = httpResponse.body().readAllBytes();
            long contentLength = fileData.length;
            String contentType = httpResponse.headers()
                    .firstValue("content-type")
                    .orElse("application/octet-stream");

            // S3에 저장할 키 생성
            String key = S3KeyGenerator.generateKey(fileData);

            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();


            // 이미 있으면 이미지 업로드 스킵
            try {
                s3Client.headObject(headObjectRequest);
                return endpoint + "/" + bucketName + "/" + key;
            } catch (NoSuchKeyException ignored) {

            }
            
            // S3 업로드 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentLength(contentLength)
                    .contentType(contentType)
                    .build();

            // 업로드 수행
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

            return endpoint + "/" + bucketName + "/" + key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image from URL: " + imageUrl, e);
        }
    }
}
