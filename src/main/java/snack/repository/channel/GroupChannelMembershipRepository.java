package snack.repository.channel;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import snack.domain.channel.GroupChannel;
import snack.domain.channel.GroupChannelMembership;
import snack.domain.user.User;

public interface GroupChannelMembershipRepository extends CrudRepository<GroupChannelMembership, Integer> {
    Collection<GroupChannelMembership> findByMember(User member);

    Collection<GroupChannelMembership> findByChannel(GroupChannel channel);

    Optional<GroupChannelMembership> findByMemberAndChannel(User member, GroupChannel channel);

    @Query("SELECT m.member.id FROM #{#entityName} m WHERE m.channel.id = ?1")
    Collection<String> getMemberIds(Integer channelId);

    Optional<GroupChannelMembership> findByMemberIdAndChannelId(String memberId, Integer channelId);
}
