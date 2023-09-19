package snack.repository.message;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import snack.domain.message.FCMRegistrationToken;
import snack.domain.user.User;

public interface FCMRegistrationTokenRepository extends CrudRepository<FCMRegistrationToken, String> {
    Collection<FCMRegistrationToken> findByUser(User user);
    Optional<FCMRegistrationToken> findByToken(String token);
}
