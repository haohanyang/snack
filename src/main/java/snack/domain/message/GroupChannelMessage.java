package snack.domain.message;

import snack.domain.channel.GroupChannel;
import snack.domain.storage.GroupChannelAttachment;
import snack.domain.user.User;
import snack.service.StorageService;
import snack.service.dto.MessageDto;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "group_channel_message")
@Table(name = "group_channel_messages")
@Data
@NoArgsConstructor
public class GroupChannelMessage {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "group_channel_message_gen", sequenceName = "group_message_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_channel_message_gen")
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
    private GroupChannel channel;

    @OneToOne
    @JoinColumn(name = "attachment_id")
    private GroupChannelAttachment attachment;

    public GroupChannelMessage(String content, User author, GroupChannel channel) {
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
