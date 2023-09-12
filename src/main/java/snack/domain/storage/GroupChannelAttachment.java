package snack.domain.storage;

import snack.domain.channel.GroupChannel;
import snack.domain.user.User;

import jakarta.persistence.*;

@Entity(name = "group_channel_attachment")
@Table(name = "group_channel_attachments", schema = "app")
@SequenceGenerator(name = "attachment_gen", sequenceName = "group_channel_attachment_seq", allocationSize = 1, schema = "app")
public class GroupChannelAttachment extends Attachment {
    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private GroupChannel channel;

    public GroupChannelAttachment() {
        super();
    }

    public GroupChannelAttachment(String key, User user, String name, Long size, String bucket, String contentType,
            GroupChannel channel) {
        super(key, user, name, size, bucket, contentType);
        this.channel = channel;
    }
}
