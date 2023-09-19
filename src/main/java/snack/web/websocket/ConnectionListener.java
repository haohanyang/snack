package snack.web.websocket;

import snack.service.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class ConnectionListener implements ApplicationListener<SessionConnectEvent> {

    private final Logger logger = LoggerFactory.getLogger(ConnectionListener.class);
    private final ConnectionService connectionService;

    public ConnectionListener(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        var accessor = StompHeaderAccessor.wrap(event.getMessage());
        var connectionId = accessor.getSessionId();
        var user = accessor.getUser();

        if (user != null) {
            var userId = user.getName();
            var currentConnectionsCount = connectionService.addConnection(userId, connectionId);
            logger.info("User {} connected, connection id {}, total connections {}", userId,
                    connectionId, currentConnectionsCount);
        }
    }

}
