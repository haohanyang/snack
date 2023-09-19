package snack.repository.message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import snack.domain.channel.UserChannel;
import snack.domain.message.UserChannelMessage;
import snack.repository.channel.UserChannelRepository;
import snack.repository.user.UserRepository;
import snack.repository.user.UserRepositoryTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserChannelMessageRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserChannelRepository userChannelRepository;

    @Autowired
    private UserChannelMessageRepository userChannelMessageRepository;

    @Test
    void testFindByEChannel() {
        var user1 = UserRepositoryTest.createTestUser();
        var user2 = UserRepositoryTest.createTestUser();

        userRepository.saveAll(Set.of(user1, user2));
        entityManager.flush();

        var userChannel = new UserChannel(user1, user2);
        userChannelRepository.save(userChannel);
        entityManager.flush();

        var message1 = new UserChannelMessage("Hallå", user1, userChannel);
        var message2 = new UserChannelMessage("Hej", user2, userChannel);
        var message3 = new UserChannelMessage("你好", user1, userChannel);
        userChannelMessageRepository.saveAll(Set.of(message1, message2, message3));
        entityManager.flush();

        var messages = userChannelMessageRepository.findByChannel(userChannel);
        assertEquals(3, messages.size());
    }

    @Test
    void testFindFirstByChannelOrderByCreatedAtDesc() {
        var user1 = UserRepositoryTest.createTestUser();
        var user2 = UserRepositoryTest.createTestUser();
        var user3 = UserRepositoryTest.createTestUser();

        userRepository.saveAll(Set.of(user1, user2, user3));
        entityManager.flush();

        var userChannel1 = new UserChannel(user1, user2);
        var userChannel2 = new UserChannel(user1, user3);
        userChannelRepository.saveAll(Set.of(userChannel1, userChannel2));
        entityManager.flush();

        var message1 = new UserChannelMessage("Hallå", user1, userChannel1);
        var message2 = new UserChannelMessage("Hej", user2, userChannel1);
        var message3 = new UserChannelMessage("你好", user1, userChannel1);
        var message4 = new UserChannelMessage("Bonjour", user2, userChannel1);

        // set message4's time to be 24 hours later
        message4.setCreatedAt(new Timestamp(message4.getCreatedAt().getTime() + 24 * 60 * 60 * 1000));
        userChannelMessageRepository.saveAll(Set.of(message1, message2, message3, message4));
        entityManager.flush();

        var channel1LastMessage = userChannelMessageRepository.findFirstByChannelOrderByCreatedAtDesc(userChannel1);
        var channel2LastMessage = userChannelMessageRepository.findFirstByChannelOrderByCreatedAtDesc(userChannel2);
        assertTrue(channel1LastMessage.isPresent());
        assertFalse(channel2LastMessage.isPresent());
        assertEquals(message4.getId(), channel1LastMessage.get().getId());
    }

}
