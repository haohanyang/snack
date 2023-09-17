package snack.repository.channel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import snack.domain.channel.GroupChannel;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupChannelRepositoryTest {

    @Autowired
    private GroupChannelRepository groupChannelRepository;

    @Autowired
    private TestEntityManager entityManager;

    public static GroupChannel createTestGroupChannel() {
        var name = "Test Group Channel";
        var description = "Test Group Channel Description";
        var groupChannel = new GroupChannel(name, description);
        return groupChannel;
    }

    @Test
    void testCreationAndSave() {
        var channel = groupChannelRepository.save(createTestGroupChannel());
        entityManager.flush();

        var found = groupChannelRepository.findById(channel.getId());
        assertTrue(found.isPresent());
        assertEquals(channel.getId(), found.get().getId());
    }
}