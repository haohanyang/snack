package snack.repository.channel;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import snack.domain.channel.GroupChannel;
import snack.domain.channel.GroupChannelMembership;
import snack.domain.user.User;

public interface GroupChannelMembershipRepository extends CrudRepository<GroupChannelMembership, Integer> {
    Set<GroupChannelMembership> findByMember(User member);

    Set<GroupChannelMembership> findByChannel(GroupChannel channel);

    Optional<GroupChannelMembership> findByMemberAndChannel(User member, GroupChannel channel);

    @Query("SELECT m.member.id FROM #{#entityName} m WHERE m.channel.id = ?1")
    Set<String> getMemberIds(Integer channelId);

    Optional<GroupChannelMembership> findByMemberIdAndChannelId(String memberId, Integer channelId);
}
