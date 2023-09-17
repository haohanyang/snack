package snack.service;

import java.util.Collection;

import snack.domain.message.FCMRegistrationToken;
import snack.domain.user.User;
import snack.service.dto.MessageDto;

public interface NotificationService {

    /**
     * Store the FCM registration token for a user
     * 
     * @param userId
     * @param token
     * @throws Exception
     */
    void storeFCMRegistrationToken(String userId, String token) throws Exception;

    Collection<FCMRegistrationToken> getDeviceTokens(Collection<User> users) throws Exception;

    Collection<FCMRegistrationToken> getDeviceTokens(User user) throws Exception;

    void sendMessageNotification(Collection<User> receivers, MessageDto message) throws Exception;

}
