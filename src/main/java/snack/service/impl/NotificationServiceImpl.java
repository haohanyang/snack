package snack.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import snack.domain.message.FCMRegistrationToken;
import snack.domain.user.User;
import snack.repository.message.FCMRegistrationTokenRepository;
import snack.repository.user.UserRepository;
import snack.service.NotificationService;
import snack.service.dto.MessageDto;
import snack.service.exception.InvalidUserException;
import software.amazon.awssdk.services.sns.SnsClient;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final FCMRegistrationTokenRepository fcmRegistrationTokenRepository;
    private final SnsClient snsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aws-sns-platformapp-arn}")
    private String snsPlatformApplicationArn;

    public NotificationServiceImpl(
            FCMRegistrationTokenRepository fcmRegistrationTokenRepository,
            SnsClient snsClient,
            UserRepository userRepository) {
        this.fcmRegistrationTokenRepository = fcmRegistrationTokenRepository;
        this.snsClient = snsClient;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void storeFCMRegistrationToken(String userId, String token) throws Exception {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserException(userId));
        var existingToken = fcmRegistrationTokenRepository.findByToken(token);
        if (existingToken.isPresent()) {
            var existingUser = existingToken.get().getUser();
            if (!existingUser.equals(user)) {
                throw new IllegalArgumentException(
                        "Token " + token + " is already registered to user "
                                + existingUser.getId());
            }
        } else {
            // Create AWS SNS platform endpoint
            var response = snsClient.createPlatformEndpoint(
                    builder -> builder
                            .platformApplicationArn(snsPlatformApplicationArn)
                            .token(token)
                            .customUserData("id:" + user.getId())
                            .attributes(
                                    Map.of(
                                            "UserId", user.getId())));
            var fcmToken = new FCMRegistrationToken();
            fcmToken.setToken(token);
            fcmToken.setUser(user);
            fcmToken.setSnsEndpointArn(response.endpointArn());
            fcmRegistrationTokenRepository.save(fcmToken);
            log.info("Saved registeration token {} of user {}", token, user.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<FCMRegistrationToken> getDeviceTokens(User user) throws Exception {
        var tokens = fcmRegistrationTokenRepository.findByUser(user);
        return tokens;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<FCMRegistrationToken> getDeviceTokens(Collection<User> users) throws Exception {
        var tokens = new LinkedList<FCMRegistrationToken>();
        users.forEach(user -> {
            var userTokens = fcmRegistrationTokenRepository.findByUser(user);
            tokens.addAll(userTokens);
        });
        return tokens;
    }

    @Override
    @Async
    public void sendMessageNotification(Collection<User> receivers, MessageDto message) throws Exception {
        var tokens = getDeviceTokens(receivers);
        var content = message.author().getFullName() + ": " + message.content();

        var messageObject = new HashMap<String, String>();
        messageObject.put("default", content);
        messageObject.put("GCM", objectMapper.writeValueAsString(
                new GCMMessage(
                        new GCMNotification(
                                "New message",
                                content))));

        var json = objectMapper.writeValueAsString(messageObject);
        tokens.forEach(token -> {
            snsClient.publish(builder -> builder
                    .message(json)
                    .messageStructure("json")
                    .targetArn(token.getSnsEndpointArn()));
            log.info("Publish notification: user {}, device {}, endpoint {}", token.getUser().getId(),
                    token.getToken(), token.getSnsEndpointArn());
        });
    }

    static record GCMMessage(GCMNotification notification) {
    }

    static record GCMNotification(String title, String body) {
    }
}
