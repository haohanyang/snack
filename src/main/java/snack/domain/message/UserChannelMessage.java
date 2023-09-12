package snack.domain.message;

import snack.domain.channel.UserChannel;
import snack.domain.storage.UserChannelAttachment;
import snack.domain.user.User;
import snack.service.StorageService;
import snack.service.dto.MessageDto;
import jakarta.persistence.*;

@Entity(name = "user_message")
@Table(name = "user_messages", schema = "app")
@SequenceGenerator(name = "message_gen", sequenceName = "user_message_seq", allocationSize = 1, schema = "app")
public class UserChannelMessage extends Message {

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private UserChannel channel;

    @OneToOne
    @JoinColumn(name = "attachment_id")
    private UserChannelAttachment attachment;

    public UserChannelMessage() {
        super();
    }

    public UserChannelMessage(User author, String content, UserChannel channel) {
        super(author, content);
        this.channel = channel;
    }

    public UserChannel getChannel() {
        return channel;
    }

    public UserChannelAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(UserChannelAttachment attachment) {
        this.attachment = attachment;
    }

    public void setChannel(UserChannel channel) {
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
