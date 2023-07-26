package snack.domain.storage;

import snack.domain.channel.GroupChannel;
import snack.domain.user.User;
import snack.service.dto.AttachmentDto;
import jakarta.persistence.*;

@Entity(name = "group_channel_attachment")
@Table(name = "group_channel_attachments", schema = "app")
@SequenceGenerator(name = "attachment_gen", sequenceName = "group_channel_attachment_seq", allocationSize = 1)
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

    public AttachmentDto toDto() {
        var url = String.format("/api/v1/channels/group/%d/attachments/%s/%s", channel.getId(), getKey(), getName());
        return new AttachmentDto(getId(), url, channel.toInfo(), getName(), getContentType());
    }
}
