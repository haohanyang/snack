package snack.service;

import snack.service.dto.FileUploadResult;

import java.net.URL;

import org.apache.commons.lang3.tuple.Triple;

public interface StorageService {

    FileUploadResult getFileUploadResult(FileUploadResult result, String userId) throws Exception;

    String getDownloadUrl(String key);

    /**
     * Get the pre-signed URL for uploading a file to S3, bucket name and key name
     * 
     * @param userId
     * @param contentType
     * @return Triple<String, String, String> - bucket name, key name, pre-signed
     *         URL
     */
    Triple<String, String, URL> getUploadUrl(String userId, String contentType);
}
