package snack.repository.channel;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import snack.domain.channel.UserChannel;
import snack.domain.user.User;

public interface UserChannelRepository extends JpaRepository<UserChannel, Integer> {
    Optional<UserChannel> findByUser1AndUser2(User user1, User user2);

    Collection<UserChannel> findByUser1OrUser2(User user1, User user2);
}
