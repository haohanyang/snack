package snack.domain.channel;

import snack.service.StorageService;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import snack.domain.message.UserChannelMessage;
import snack.domain.storage.UserChannelAttachment;
import snack.domain.user.User;
import snack.service.dto.ChannelInfo;
import snack.service.dto.ChannelType;
import snack.service.dto.UserChannelDto;

@Entity(name = "user_channel")
@Table(name = "user_channels")
@Data
@NoArgsConstructor
public class UserChannel {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "user_channel_gen", sequenceName = "user_channel_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_channel_gen")
    private Integer id;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    // user1.id < user2.id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user2_id")
    private User user2;

    @OneToMany(mappedBy = "channel")
    private Set<UserChannelAttachment> userChannelAttachments = new HashSet<>();

    public UserChannel(User user1, User user2) {
        if (user1.getId().compareTo(user2.getId()) < 0) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
    }

    public ChannelInfo toInfo() {
        return new ChannelInfo(
                getId(),
                ChannelType.USER);
    }

    public UserChannelDto toDto(StorageService storageService, @Nullable UserChannelMessage lastMessage) {
        var lastUpdated = lastMessage == null ? getCreatedAt() : lastMessage.getCreatedAt();
        var lastMessageDto = lastMessage == null ? null : lastMessage.toDto(storageService);
        return new UserChannelDto(
                getId(),
                ChannelType.USER,
                lastMessageDto,
                lastUpdated.toString(),
                getUser1().toDto(false),
                getUser2().toDto(false), 0);
    }
}
