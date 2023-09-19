package snack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import snack.security.websocket.SubscriptionInterceptor;

import java.util.Objects;

@Configuration
@EnableWebSecurity
@EnableWebSocketMessageBroker
public class WehSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final Environment env;
    private final SubscriptionInterceptor subscriptionInterceptor;

    public WehSocketConfig(SubscriptionInterceptor subscriptionInterceptor, Environment env) {
        this.subscriptionInterceptor = subscriptionInterceptor;
        this.env = env;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/enable.html
        // The client connects to localhost:8080/ws and, once a WebSocket connection is
        // established,
        // STOMP frames begin to flow on it.

        var activeProfile = env.getProperty("spring.profiles.active");

        if (Objects.equals(activeProfile, "dev")) {
            // Allow cors only in dev env
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("*")
                    .withSockJS();
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("*");
        } else {
            registry.addEndpoint("/ws")
                    .withSockJS();
            registry.addEndpoint("/ws");
        }

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/enable.html
        // The client sends a SUBSCRIBE frame with a destination header of
        // /gateway/{id}. Once received and decoded, the message is sent to the
        // clientInboundChannel and is then routed to the message broker, which stores
        // the client subscription.
        registry.enableSimpleBroker("/gateway");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(this.subscriptionInterceptor);
    }
}
