package snack.domain.storage;

import snack.domain.channel.UserChannel;
import snack.domain.user.User;
import snack.service.dto.AttachmentDto;
import jakarta.persistence.*;

@Entity(name = "user_channel_attachment")
@Table(name = "user_channel_attachments", schema = "app")
@SequenceGenerator(name = "attachment_gen", sequenceName = "user_channel_attachment_seq", allocationSize = 1)
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

    public AttachmentDto toDto() {
        var url = String.format("/api/v1/channels/user/%d/attachments/%s/%s", channel.getId(), getKey(), getName());
        return new AttachmentDto(getId(), url, channel.toInfo(), getName(), getContentType());
    }
}
