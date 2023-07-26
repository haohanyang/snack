package snack.service.impl;

import java.util.concurrent.ConcurrentHashMap;

import snack.service.ConnectionService;
import org.springframework.stereotype.Service;

@Service
public class ConnectionServiceImpl implements ConnectionService {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Boolean>> connections =
        new ConcurrentHashMap<>();

    @Override
    public int addConnection(String userId, String sessionId) {
        var connectionSet = connections.compute(userId, (id, userConnections) -> {
            if (userConnections == null) {
                userConnections = new ConcurrentHashMap<>();
            }
            userConnections.put(sessionId, true);
            return userConnections;
        });
        return connectionSet.size();
    }

    @Override
    public int removeConnection(String userId, String sessionId) {
        var connectionSet = connections.compute(userId, (id, userConnections) -> {
            if (userConnections != null) {
                userConnections.remove(sessionId);
                if (userConnections.size() == 0) {
                    return null;
                }
            }
            return userConnections;
        });
        if (connectionSet != null) {
            return connectionSet.size();
        }
        return 0;
    }

}
