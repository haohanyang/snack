package snack.service;

import snack.service.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import snack.service.dto.AttachmentDto;
import snack.service.dto.ChannelInfo;
import snack.service.dto.UserAssetDto;
import io.awspring.cloud.s3.S3Resource;

public interface StorageService {
    FileUploadResult uploadFile(MultipartFile file, String userId)
        throws Exception;

    FileUploadResult getFileUploadResult(FileUploadResult result, String userId) throws Exception;

    String getDownloadUrl(String key);
}
