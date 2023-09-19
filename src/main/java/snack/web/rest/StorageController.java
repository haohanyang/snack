package snack.web.rest;

import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import snack.service.StorageService;

@RestController
@RequestMapping("/api/v1")
public class StorageController {
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("presigned-url")
    public PreSignedUrlResponse generatePreSignedUrl(@RequestBody PreSignedUrlRequest request,
            @AuthenticationPrincipal Jwt principal) {
        var info = storageService.getUploadUrl(principal.getSubject(), request.contentType);
        var fileUrl = storageService.getDownloadUrl(info.getMiddle());
        return new PreSignedUrlResponse(info.getLeft(), info.getMiddle(), info.getRight().toString(), fileUrl);
    }

    static record PreSignedUrlRequest(String contentType) {
    }

    static record PreSignedUrlResponse(String bucket, String key, String url, String fileUrl) {
    }
}
