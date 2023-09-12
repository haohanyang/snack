package snack.domain.channel;

import snack.service.StorageService;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import snack.domain.message.UserChannelMessage;
import snack.domain.storage.UserChannelAttachment;
import snack.domain.user.User;
import snack.service.dto.ChannelInfo;
import snack.service.dto.ChannelType;
import snack.service.dto.UserChannelDto;

@Entity(name = "user_channel")
@Table(name = "user_channels", schema = "app")
@SequenceGenerator(name = "channel_gen", sequenceName = "user_channel_seq", allocationSize = 1, schema = "app")
public class UserChannel extends Channel {

    // user1.id < user2.id
    @ManyToOne(optional = false)
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user2_id")
    private User user2;

    @OneToMany(mappedBy = "channel")
    private Set<UserChannelAttachment> userChannelAttachments = new HashSet<>();

    public UserChannel() {
        super();
    }

    public UserChannel(User user1, User user2) {
        super();
        if (user1.getId().compareTo(user2.getId()) < 0) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public Set<UserChannelAttachment> getUserChannelAttachments() {
        return userChannelAttachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        var channel = (UserChannel) o;

        return getId().equals(channel.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
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
                getUser1().toDto(),
                getUser2().toDto(), 0);
    }

    public static UserChannel createTestUserChannel() {
        var userChannel = new UserChannel();
        return userChannel;
    }
}
