package snack.config;

import snack.security.websocket.SubscriptionInterceptor;
import org.springframework.context.annotation.Configuration;

import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/enable.html
        // The client connects to localhost:8080/ws and, once a WebSocket connection is established,
        // STOMP frames begin to flow on it.
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // https://docs.spring.io/spring-framework/reference/web/websocket/stomp/enable.html
        // The client sends a SUBSCRIBE frame with a destination header of /gateway/{id}. Once received and decoded, the message is sent to the clientInboundChannel and is then routed to the message broker, which stores the client subscription.
        registry.enableSimpleBroker("/gateway");
    }

    // Configure messages received from WebSocket clients.
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SubscriptionInterceptor());
    }
}
