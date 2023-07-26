package snack.web.requests;

import snack.service.dto.FileUploadResult;
import jakarta.annotation.Nullable;

public record UpdateProfileRequest(
                String userId,
                String firstName,
                String lastName,
                String bio,
                String status,
                @Nullable FileUploadResult avatar,
                @Nullable FileUploadResult backgroundImage) {
}
