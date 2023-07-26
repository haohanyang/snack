package snack.repository.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import snack.domain.channel.GroupChannel;

public interface GroupChannelRepository extends JpaRepository<GroupChannel, Integer> {

}
