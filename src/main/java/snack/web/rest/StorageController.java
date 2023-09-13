package snack.web.rest;

import org.springframework.security.oauth2.jwt.Jwt;
import snack.service.dto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import snack.service.StorageService;

@RestController
@RequestMapping("/api/v1")
public class StorageController {

    private final Logger logger = LoggerFactory.getLogger(StorageController.class);
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("files")
    public FileUploadResult uploadChannelAttachment(@RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt principal)
            throws Exception {
        var userId = principal.getSubject();
        var attachment = storageService.uploadFile(file, userId);
        logger.info("User {} uploaded a file", userId);
        return attachment;
    }

    @PostMapping("presigned-url")
    public PreSignedUrlResponse generatePreSignedUrl(@RequestBody PreSignedUrlRequest request,
            @AuthenticationPrincipal Jwt principal) {
        var info = storageService.getUploadUrl(principal.getSubject(), request.contentType);
        var fileUrl = storageService.getDownloadUrl(info.getLeft(), info.getMiddle());
        return new PreSignedUrlResponse(info.getLeft(), info.getMiddle(), info.getRight(), fileUrl);
    }

    static record PreSignedUrlRequest(String contentType) {
    }

    static record PreSignedUrlResponse(String bucket, String key, String url, String fileUrl) {
    }
}
