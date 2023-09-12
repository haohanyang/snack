package snack.service.dto;

import jakarta.annotation.Nullable;

public record UserChannelDto(
    Integer id,
    ChannelType type,
    @Nullable MessageDto lastMessage,
    String lastUpdated,
    UserDto user1,
    UserDto user2,
    Integer unreadMessagesCount
) {
}
