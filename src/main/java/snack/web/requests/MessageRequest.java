package snack.web.requests;

import snack.service.dto.ChannelInfo;
import snack.service.dto.FileUploadResult;
import jakarta.annotation.Nullable;

public record MessageRequest(
        ChannelInfo channel,
        String authorId,
        String content,
        @Nullable FileUploadResult fileUploadResult) {
}
