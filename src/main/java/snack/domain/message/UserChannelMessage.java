package snack.domain.message;

import snack.domain.channel.UserChannel;
import snack.domain.storage.UserChannelAttachment;
import snack.domain.user.User;
import snack.service.StorageService;
import snack.service.dto.MessageDto;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_channel_message")
@Table(name = "user_channel_messages")
@Data
@NoArgsConstructor
public class UserChannelMessage {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "user_channel_message_gen", sequenceName = "user_message_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_channel_message_gen")
    private Integer id;

    @Column(name = "content", length = 500)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private UserChannel channel;

    @OneToOne
    @JoinColumn(name = "attachment_id")
    private UserChannelAttachment attachment;

    public UserChannelMessage(String content, User author, UserChannel channel) {
        this.content = content;
        this.author = author;
        this.channel = channel;
    }

    public MessageDto toDto(StorageService storageService) {
        var attachment = getAttachment();
        return new MessageDto(
                getId(),
                getAuthor().toDto(false),
                getChannel().toInfo(),
                getContent(),
                getCreatedAt().toString(),
                attachment == null ? null : getAttachment().toDto(storageService));
    }
}
