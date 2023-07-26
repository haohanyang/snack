package snack.repository.message;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import snack.domain.channel.UserChannel;
import snack.domain.message.UserChannelMessage;

public interface UserMessageRepository extends JpaRepository<UserChannelMessage, Integer> {
    Collection<UserChannelMessage> findByChannel(UserChannel channel);

    Optional<UserChannelMessage> findFirstByChannelOrderByCreatedAtDesc(UserChannel channel);
}
