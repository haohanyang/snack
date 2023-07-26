package snack.web.rest;

import snack.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
                                                    @AuthenticationPrincipal OidcUser principal)
        throws Exception {
        var userId = principal.getSubject();
        var attachment = storageService.uploadFile(file, userId);
        logger.info("User {} uploaded a file", userId);
        return attachment;
    }
}
