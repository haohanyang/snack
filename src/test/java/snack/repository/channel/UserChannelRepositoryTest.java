package snack.repository.channel;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import snack.domain.channel.UserChannel;
import snack.repository.user.UserRepository;
import snack.repository.user.UserRepositoryTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserChannelRepositoryTest {

    @Autowired
    private UserChannelRepository userChannelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreationAndSave() {
        var user1 = UserRepositoryTest.createTestUser();
        var user2 = UserRepositoryTest.createTestUser();

        userRepository.saveAll(Set.of(user1, user2));
        entityManager.flush();

        var channel = new UserChannel(user1, user2);

        userChannelRepository.save(channel);
        entityManager.flush();

        var found = userChannelRepository.findById(channel.getId());
        assertTrue(found.isPresent());
        assertTrue(found.get().getUser1().getId().compareTo(found.get().getUser2().getId()) < 0);
    }

    @Test
    void testFindByUser1AndUser2() {
        var user1_ = UserRepositoryTest.createTestUser();
        var user2_ = UserRepositoryTest.createTestUser();

        userRepository.saveAll(Set.of(user1_, user2_));

        var user1 = user1_.getId().compareTo(user2_.getId()) < 0 ? user1_ : user2_;
        var user2 = user1_.getId().compareTo(user2_.getId()) < 0 ? user2_ : user1_;

        entityManager.flush();

        var channel = new UserChannel(user1, user2);

        userChannelRepository.save(channel);
        entityManager.flush();

        var found = userChannelRepository.findByUser1AndUser2(user1, user2);
        assertTrue(found.isPresent());
    }

    @Test
    void testFindByUser1OrUser2() {
        var user1 = UserRepositoryTest.createTestUser();
        var user2 = UserRepositoryTest.createTestUser();
        var user3 = UserRepositoryTest.createTestUser();
        var user4 = UserRepositoryTest.createTestUser();

        userRepository.saveAll(Set.of(user1, user2, user3, user4));
        entityManager.flush();

        var channel1 = new UserChannel(user1, user2);
        var channel2 = new UserChannel(user3, user4);
        var channel3 = new UserChannel(user3, user1);
        var channel4 = new UserChannel(user2, user4);
        var channel5 = new UserChannel(user1, user4);

        userChannelRepository.saveAll(Set.of(channel1, channel2, channel3, channel4, channel5));

        var user1Channels = userChannelRepository.findByUser1OrUser2(user1, user1);
        assertTrue(user1Channels.size() == 3);

        var user2Channels = userChannelRepository.findByUser1OrUser2(user2, user2);
        assertTrue(user2Channels.size() == 2);

        var user3Channels = userChannelRepository.findByUser1OrUser2(user3, user3);
        assertTrue(user3Channels.size() == 2);

        var user4Channels = userChannelRepository.findByUser1OrUser2(user4, user4);
        assertTrue(user4Channels.size() == 3);
    }
}
