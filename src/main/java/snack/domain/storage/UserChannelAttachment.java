package snack.domain.storage;

import snack.domain.channel.UserChannel;
import snack.domain.user.User;
import jakarta.persistence.*;

@Entity(name = "user_channel_attachment")
@Table(name = "user_channel_attachments", schema = "app")
@SequenceGenerator(name = "attachment_gen", sequenceName = "user_channel_attachment_seq", allocationSize = 1, schema = "app")
public class UserChannelAttachment extends Attachment {

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private UserChannel channel;

    public UserChannelAttachment() {
        super();
    }

    public UserChannelAttachment(String key, User user, String name, Long size, String bucket, String contentType,
            UserChannel channel) {
        super(key, user, name, size, bucket, contentType);
        this.channel = channel;
    }
}
