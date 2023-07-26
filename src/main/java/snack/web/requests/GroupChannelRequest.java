package snack.web.requests;

import java.util.Collection;

public record GroupChannelRequest(
        String name,
        String description,
        String creatorId,
        Collection<String> memberIds) {
}
