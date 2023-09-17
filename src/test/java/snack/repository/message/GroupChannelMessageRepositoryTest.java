package snack.repository.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import snack.domain.message.GroupChannelMessage;
import snack.repository.channel.GroupChannelRepository;
import snack.repository.channel.GroupChannelRepositoryTest;
import snack.repository.user.UserRepository;
import snack.repository.user.UserRepositoryTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupChannelMessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupChannelMessageRepository groupChannelMessageRepository;

    @Autowired
    private GroupChannelRepository groupChannelRepository;

    @Test
    void testFindByChannel() {
        var user1 = UserRepositoryTest.createTestUser();
        var user2 = UserRepositoryTest.createTestUser();
        var user3 = UserRepositoryTest.createTestUser();
        var user4 = UserRepositoryTest.createTestUser();

        userRepository.saveAll(Set.of(user1, user2, user3, user4));
        entityManager.flush();

        var groupChannel = GroupChannelRepositoryTest.createTestGroupChannel();
        groupChannelRepository.save(groupChannel);
        entityManager.flush();

        var message1 = new GroupChannelMessage("Hallå", user1, groupChannel);
        var message2 = new GroupChannelMessage("Hej", user2, groupChannel);
        var message3 = new GroupChannelMessage("你好", user3, groupChannel);
        var message4 = new GroupChannelMessage("Bonjour", user4, groupChannel);
        var message5 = new GroupChannelMessage("Hola", user1, groupChannel);
        var message6 = new GroupChannelMessage("Ciao", user2, groupChannel);
        var message7 = new GroupChannelMessage("سلام", user3, groupChannel);
        var message8 = new GroupChannelMessage("こんにちは", user4, groupChannel);
        var message9 = new GroupChannelMessage("안녕하세요", user1, groupChannel);

        groupChannelMessageRepository.saveAll(
                Set.of(message1, message2, message3, message4, message5, message6, message7, message8, message9));

        entityManager.flush();

        var messages = groupChannelMessageRepository.findByChannel(groupChannel);
        assertEquals(9, messages.size());
    }

}
