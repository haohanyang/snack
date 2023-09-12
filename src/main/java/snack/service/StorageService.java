package snack.service;

import snack.service.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;


public interface StorageService {
    FileUploadResult uploadFile(MultipartFile file, String userId)
        throws Exception;

    FileUploadResult getFileUploadResult(FileUploadResult result, String userId) throws Exception;

    String getDownloadUrl(String bucket, String key);
}
