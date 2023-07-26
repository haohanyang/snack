package snack.service.dto;

import java.util.Collection;

public record Channels(
        Collection<UserChannelDto> userChannels,
        Collection<GroupChannelDto> groupChannels) {
}
