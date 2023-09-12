package snack.repository.message;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import snack.domain.channel.GroupChannel;
import snack.domain.message.GroupChannelMessage;

public interface GroupMessageRepository extends JpaRepository<GroupChannelMessage, Integer> {
    Collection<GroupChannelMessage> findByChannel(GroupChannel channel);

    Optional<GroupChannelMessage> findFirstByChannelOrderByCreatedAtDesc(GroupChannel channel);
}
