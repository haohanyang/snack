package snack.service.dto;

import jakarta.annotation.Nullable;

public record MessageDto(
    Integer id,
    UserDto author,
    ChannelInfo channel,
    String content,
    String createdAt,
    @Nullable String attachmentUri) {
}
