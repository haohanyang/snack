package snack.security.websocket;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SubscriptionInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public SubscriptionInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    @Nullable
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            var token = accessor.getFirstNativeHeader("Authorization");
            if (token != null) {
                var jwt = jwtDecoder.decode(token);
                var converter = new JwtAuthenticationConverter();
                Authentication authentication = converter.convert(jwt);
                accessor.setUser(authentication);
            }
        }

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // Make sure users only subscribe to their own channels
            var user = accessor.getUser();
            var destination = accessor.getDestination();
            if (user == null) {
                throw new IllegalArgumentException("User must be authenticated");
            }
            if (!Objects.equals(destination, "/gateway/" + user.getName())) {
                throw new IllegalArgumentException("Illegal destination of subscription");
            }
        }
        return message;
    }

}
