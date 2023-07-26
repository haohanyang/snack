package snack.service;

public interface ConnectionService {
    /**
     * Add a new websocket connection, return the total number of connections for the user
     *
     * @param userId    user id
     * @param sessionId websocket session id
     * @return total number of connections for the user
     */
    int addConnection(String userId, String sessionId);


    /**
     * Remove the websocket connection, return the total number of connections for the user
     *
     * @param userId    user id
     * @param sessionId websocket session id
     * @return total number of connections for the user
     */

    int removeConnection(String userId, String sessionId);
}
