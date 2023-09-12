package snack.service.impl;

import java.util.UUID;

import snack.service.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import snack.service.StorageService;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;

@Service
public class StorageServiceImpl implements StorageService {
    private final S3Template s3Template;

    @Value("${aws-s3-bucket}")
    private String bucket;

    @Value("${aws-cloudfront-domain}")
    private String cloudFrontDomain;

    public StorageServiceImpl(
        S3Template s3Template) {
        this.s3Template = s3Template;
    }

    @Override
    public FileUploadResult uploadFile(MultipartFile file, String userId) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }

        var inputStream = file.getInputStream();
        var key = UUID.randomUUID().toString();
        var resource = s3Template.upload(bucket, key, inputStream,
            ObjectMetadata.builder()
                .contentType(file.getContentType())
                .metadata("uploader", userId)
                .build());
        return new FileUploadResult(
            getDownloadUrl(bucket, key),
            bucket,
            key,
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType());
    }

    @Override
    public FileUploadResult getFileUploadResult(FileUploadResult result, String userId) throws Exception {
        var resource = s3Template.download(result.bucket(), result.key());
        if (!resource.exists()) {
            throw new IllegalArgumentException("File doesn't exist");
        }
        if (!userId.equals(resource.metadata().get("uploader"))) {
            throw new IllegalArgumentException("User doesn't match");
        }
        return new FileUploadResult(
            getDownloadUrl(result.bucket(), result.key()),
            result.bucket(),
            result.key(),
            result.fileName(),
            resource.contentLength(),
            resource.contentType()
        );
    }

    @Override
    public String getDownloadUrl(String bucket, String key) {
        return "https://" + cloudFrontDomain + "/" + key;
    }
}
