package snack.repository.storage;

import org.springframework.data.repository.CrudRepository;
import snack.domain.storage.UserChannelAttachment;

public interface UserChannelAttachmentRepository extends CrudRepository<UserChannelAttachment, Integer> {

}
