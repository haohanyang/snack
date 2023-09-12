package snack.domain.message;

import snack.domain.channel.GroupChannel;
import snack.domain.storage.GroupChannelAttachment;
import snack.domain.user.User;
import snack.service.StorageService;
import snack.service.dto.MessageDto;
import jakarta.persistence.*;

@Entity(name = "group_message")
@Table(name = "group_messages", schema = "app")
@SequenceGenerator(name = "message_gen", sequenceName = "group_message_seq", allocationSize = 1, schema = "app")
public class GroupChannelMessage extends Message {
    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private GroupChannel channel;

    @OneToOne
    @JoinColumn(name = "attachment_id")
    private GroupChannelAttachment attachment;

    public GroupChannelMessage() {
        super();
    }

    public GroupChannelMessage(User author, String content, GroupChannel channel) {
        super(author, content);
        this.channel = channel;
    }

    public GroupChannelAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(GroupChannelAttachment attachment) {
        this.attachment = attachment;
    }

    public GroupChannel getChannel() {
        return channel;
    }

    public void setChannel(GroupChannel channel) {
        this.channel = channel;
    }

    public MessageDto toDto(StorageService storageService) {
        var attachment = getAttachment();
        return new MessageDto(
            getId(),
            getAuthor().toDto(),
            getChannel().toInfo(),
            getContent(),
            getCreatedAt().toString(),
            attachment == null ? null : getAttachment().toDto(storageService));
    }

}
