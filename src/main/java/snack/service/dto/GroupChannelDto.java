package snack.service.dto;

import jakarta.annotation.Nullable;

public record GroupChannelDto(
    Integer id,
    ChannelType type,
    @Nullable MessageDto lastMessage,
    String lastUpdated,
    String name,
    String avatar,
    String description,
    String createdAt,
    Integer memberCount,
    Integer unreadMessagesCount
) {
}
