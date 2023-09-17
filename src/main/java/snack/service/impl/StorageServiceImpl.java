package snack.service.impl;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

import snack.service.dto.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import snack.service.StorageService;

@Service
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${aws-s3-bucket}")
    private String bucket;

    @Value("${aws-cloudfront-base-url}")
    private String cloudFrontBaseUrl;

    public StorageServiceImpl(S3Client s3Client, S3Presigner presigner) {
        this.s3Client = s3Client;
        this.presigner = presigner;
    }

    @Override
    public FileUploadResult getFileUploadResult(FileUploadResult result, String userId) throws Exception {
        var data = s3Client.headObject(builder -> builder.bucket(result.bucket())
                .key(result.key()));

        if (!userId.equals(data.metadata().get("user"))) {
            throw new IllegalArgumentException("User doesn't match");
        }
        return new FileUploadResult(
                getDownloadUrl(result.key()),
                result.bucket(),
                result.key(),
                result.fileName(),
                data.contentLength(),
                data.contentType());
    }

    @Override
    public String getDownloadUrl(String key) {
        return cloudFrontBaseUrl + "/" + key;
    }

    @Override
    public Triple<String, String, URL> getUploadUrl(String userId, String contentType) {

        var key = "user-files" + "/" + UUID.randomUUID().toString();
        var metadata = new HashMap<String, String>();
        metadata.put("user", userId);

        var objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .metadata(metadata)
                .build();

        var presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(30))
                .putObjectRequest(objectRequest)
                .build();

        var presignedRequest = presigner.presignPutObject(presignRequest);
        var url = presignedRequest.url();
        return Triple.of(bucket, key, url);
    }
}
