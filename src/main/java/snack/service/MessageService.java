package snack.service;

import java.util.Collection;

import org.springframework.data.util.Pair;

import snack.domain.user.User;
import snack.service.dto.MessageDto;
import snack.service.exception.ChannelNotFoundException;
import snack.web.requests.MessageRequest;
import jakarta.annotation.Nullable;

public interface MessageService {

        /**
         * Store a user channel message in the database
         *
         * @param request the request to send a message
         * @return The message stored and the receivers of the message
         */
        Pair<MessageDto, Collection<User>> storeUserChannelMessage(MessageRequest request) throws Exception;

        /**
         * Store a group channel message in the database
         *
         * @param request the request to send a message
         * @return The message stored and the receivers of the message
         */
        Pair<MessageDto, Collection<User>> storeGroupChannelMessage(MessageRequest request) throws Exception;

        /**
         * Get all messages in a user channel. Also check if the requester is in the
         * channel.
         *
         * @param channelId the id of the user channel
         * @return a collection of messages
         * @throws ChannelNotFoundException if the channel does not exist
         */

        Collection<MessageDto> getUserMessages(Integer channelId, @Nullable String requesterId)
                        throws ChannelNotFoundException, IllegalArgumentException;

        /**
         * Get all messages in a group channel. Also check if the requester is in the
         * channel.
         *
         * @param channelId the id of the group channel
         * @return a collection of messages
         * @throws ChannelNotFoundException if the channel does not exist
         */
        Collection<MessageDto> getGroupMessages(Integer channelId, @Nullable String requesterId)
                        throws ChannelNotFoundException, IllegalArgumentException;

        void sendMessage(Collection<String> destinations, MessageDto message);
}
