package snack.repository.channel;

import snack.domain.channel.GroupChannelMembership;
import snack.repository.user.UserRepository;
import snack.repository.user.UserRepositoryTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupChannelMembershipRepositoryTest {
    @Autowired
    private GroupChannelMembershipRepository groupChannelMembershipRepository;

    @Autowired
    private GroupChannelRepository groupChannelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testfindByChannel() {
        var user1 = UserRepositoryTest.createTestUser();
        var user2 = UserRepositoryTest.createTestUser();
        var user3 = UserRepositoryTest.createTestUser();

        userRepository.saveAll(List.of(user1, user2, user3));

        var groupChannel = GroupChannelRepositoryTest.createTestGroupChannel();
        groupChannelRepository.save(groupChannel);

        entityManager.flush();

        var membership1 = new GroupChannelMembership(user1, groupChannel, true);
        var membership2 = new GroupChannelMembership(user2, groupChannel, false);
        var membership3 = new GroupChannelMembership(user3, groupChannel, false);

        groupChannelMembershipRepository.saveAll(Set.of(membership1, membership2, membership3));
        entityManager.flush();

        var groupMemberships = groupChannelMembershipRepository.findByChannel(groupChannel);
        assertEquals(3, groupMemberships.size());
    }

    @Test
    void testFindByMember() {
        var user1 = UserRepositoryTest.createTestUser();
        var user2 = UserRepositoryTest.createTestUser();
        userRepository.saveAll(Set.of(user1, user2));

        var group1 = GroupChannelRepositoryTest.createTestGroupChannel();
        var group2 = GroupChannelRepositoryTest.createTestGroupChannel();
        var group3 = GroupChannelRepositoryTest.createTestGroupChannel();
        // Using Set.of() here causes an error
        groupChannelRepository.saveAll(List.of(group1, group2, group3));

        entityManager.flush();
        var membership1 = new GroupChannelMembership(user1, group1, false);
        var membership2 = new GroupChannelMembership(user1, group2, false);
        var membership3 = new GroupChannelMembership(user1, group3, false);
        var membership4 = new GroupChannelMembership(user2, group1, false);
        var membership5 = new GroupChannelMembership(user2, group3, false);

        groupChannelMembershipRepository
                .saveAll(Set.of(membership1, membership2, membership3, membership4, membership5));

        entityManager.flush();

        var user1Memberships = groupChannelMembershipRepository.findByMember(user1);
        assertEquals(3, user1Memberships.size());

        var user2Memberships = groupChannelMembershipRepository.findByMember(user2);
        assertEquals(2, user2Memberships.size());
    }
}