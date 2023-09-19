package snack.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import snack.service.ConnectionService;

@Component
public class DisconnectionListener implements ApplicationListener<SessionDisconnectEvent> {
    private final Logger logger = LoggerFactory.getLogger(DisconnectionListener.class);
    private final ConnectionService connectionService;

    public DisconnectionListener(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        var accessor = StompHeaderAccessor.wrap(event.getMessage());
        var connectionId = accessor.getSessionId();
        var user = accessor.getUser();

        if (user != null) {
            var userId = user.getName();
            var currentConnectionsCount = connectionService.removeConnection(userId, connectionId);
            logger.info("User {} disconnected, connection id {}, total connections {}", userId,
                    connectionId, currentConnectionsCount);
        }
    }
}
