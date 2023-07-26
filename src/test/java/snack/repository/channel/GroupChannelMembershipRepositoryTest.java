package snack.repository.channel;

import snack.domain.channel.GroupChannel;
import snack.domain.channel.GroupChannelMembership;
import snack.domain.user.User;
import snack.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GroupChannelMembershipRepositoryTest {
    @Autowired
    private GroupChannelMembershipRepository groupChannelMembershipRepository;

    @Autowired
    private GroupChannelRepository groupChannelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindByMember() {
        var user = User.createTestUser();
        userRepository.save(user);

        var group1 = GroupChannel.createTestGroupChannel();
        var group2 = GroupChannel.createTestGroupChannel();
        var group3 = GroupChannel.createTestGroupChannel();
        var group4 = GroupChannel.createTestGroupChannel();

        groupChannelRepository.saveAll(List.of(group1, group2, group3, group4));

        entityManager.flush();
        var membership1 = new GroupChannelMembership(user, group1, false);
        var membership2 = new GroupChannelMembership(user, group2, false);
        var membership3 = new GroupChannelMembership(user, group3, false);
        groupChannelMembershipRepository.saveAll(List.of(membership1, membership2, membership3));

        entityManager.flush();

        var found = groupChannelMembershipRepository.findByMember(user);
        assertEquals(3, found.size());
        assertTrue(found.contains(membership1));
        assertTrue(found.contains(membership2));
        assertTrue(found.contains(membership3));
    }

}