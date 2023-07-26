package snack.service.dto;

import jakarta.annotation.Nullable;

public record GroupChannelDto(
        String id,
        ChannelType type,
        @Nullable MessageDto lastMessage,
        String lastUpdated,
        String name,
        String backgroundImage,
        String description,
        String createdAt,
        Integer memberCount,
        Integer unReadMessagesCount
        ) {
}
