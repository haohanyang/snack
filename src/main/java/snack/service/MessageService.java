package snack.service;

import java.util.Collection;

import snack.service.dto.MessageDto;
import snack.service.exception.ChannelNotFoundException;
import snack.web.requests.MessageRequest;
import jakarta.annotation.Nullable;

public interface MessageService {

    /**
     * Send a message to a user channel
     *
     * @param request the request to send a message
     * @return the message sent
     */
    MessageDto sendUserChannelMessage(MessageRequest request) throws Exception;

    /**
     * Send a message to a group channel
     *
     * @param request the request to send a message
     * @return the message sent
     */
    MessageDto sendGroupChannelMessage(MessageRequest request) throws Exception;

    /**
     * Get all messages in a user channel. Also check if the requester is in the channel.
     *
     * @param channelId the id of the user channel
     * @return a collection of messages
     * @throws ChannelNotFoundException if the channel does not exist
     */

    Collection<MessageDto> getUserMessages(Integer channelId, @Nullable String requesterId) throws ChannelNotFoundException, IllegalArgumentException;

    /**
     * Get all messages in a group channel. Also check if the requester is in the channel.
     *
     * @param channelId the id of the group channel
     * @return a collection of messages
     * @throws ChannelNotFoundException if the channel does not exist
     */
    Collection<MessageDto> getGroupMessages(Integer channelId, @Nullable String requesterId) throws ChannelNotFoundException, IllegalArgumentException;
}
