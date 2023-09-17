package snack.service;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import snack.repository.message.FCMRegistrationTokenRepository;
import snack.repository.user.UserRepository;
import snack.repository.user.UserRepositoryTest;
import snack.service.dto.ChannelInfo;
import snack.service.dto.ChannelType;
import snack.service.dto.MessageDto;

@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FCMRegistrationTokenRepository fcmRegistrationTokenRepository;

    @Test
    void testStoreFCMRegistrationToken() throws Exception {
        var user = UserRepositoryTest.createTestUser();
        userRepository.save(user);

        var fmcToken = UUID.randomUUID().toString();
        notificationService.storeFCMRegistrationToken(user.getId(), fmcToken);

        var tokens = fcmRegistrationTokenRepository.findByUser(user);
        assert (tokens.size() == 1);
    }

    @Test
    void testSendMessageNotification() throws Exception {
        var userId = "";
        var user = userRepository.findById(userId);
        assert (user.isPresent());
        var msg = new MessageDto(
                0,
                user.get().toDto(false),
                new ChannelInfo(0, ChannelType.USER),
                "Test message",
                new Timestamp(System.currentTimeMillis()).toString(),
                null);
        notificationService.sendMessageNotification(Set.of(user.get()), msg);
    }
}
