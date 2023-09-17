package snack.repository.user;

import snack.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    public static User createTestUser() {
        var id = UUID.randomUUID().toString();
        var fullName = "Test User";
        var email = id + "@example.com";
        var user = new User();
        user.setId(id);
        user.setFullName(fullName);
        user.setEmail(email);
        return user;
    }

    @Test
    void testFindUserById() {
        var user = createTestUser();
        entityManager.persist(user);
        entityManager.flush();
        var found = userRepository.findById(user.getId());
        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
    }
}