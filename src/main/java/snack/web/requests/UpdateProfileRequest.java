package snack.web.requests;

import org.springframework.lang.Nullable;

import snack.service.dto.FileUploadResult;

public record UpdateProfileRequest(
        String fullName,
        String bio,
        String status,
        @Nullable FileUploadResult avatar,
        @Nullable FileUploadResult backgroundImage) {
}
