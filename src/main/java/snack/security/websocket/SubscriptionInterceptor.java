package snack.security.websocket;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Objects;

public class SubscriptionInterceptor implements ChannelInterceptor {

    @Override
    @Nullable
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // Make sure users only subscribe to their own channels
            var user = accessor.getUser();
            var destination = accessor.getDestination();
            if (user instanceof OAuth2AuthenticationToken token) {
                if (!Objects.equals(destination, "/gateway/" + token.getPrincipal().getName())) {
                    throw new IllegalArgumentException("Illegal destination of subscription");
                }
                return message;
            }
            throw new IllegalArgumentException("User is not authenticated");
        }
        return message;
    }

}
