package snack.domain.channel;

import snack.domain.message.GroupChannelMessage;
import snack.domain.storage.GroupChannelAttachment;
import snack.service.StorageService;
import snack.service.dto.ChannelInfo;
import snack.service.dto.ChannelType;
import snack.service.dto.GroupChannelDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "group_channel")
@Table(name = "group_channels", schema = "app")
@SequenceGenerator(name = "channel_gen", sequenceName = "group_channel_seq",
    allocationSize = 1, schema = "app")
public class GroupChannel extends Channel {
    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "avatar", length = 300)
    private String avatar;

    @Column(name = "group_name", length = 30, nullable = false)
    private String name;

    @OneToMany(mappedBy = "channel")
    private Set<GroupChannelMembership> groupChannelMemberships = new HashSet<>();

    @OneToMany(mappedBy = "channel")
    private Set<GroupChannelAttachment> groupChannelAttachments = new HashSet<>();

    public GroupChannel() {
        super();
    }

    public GroupChannel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        this.avatar = "https://unpkg.com/ionicons@7.1.0/dist/svg/people-circle-outline.svg";
    }

    public Set<GroupChannelMembership> getGroupChannelMemberships() {
        return groupChannelMemberships;
    }

    public void setGroupChannelMemberships(Set<GroupChannelMembership> groupChannelMemberships) {
        this.groupChannelMemberships = groupChannelMemberships;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Set<GroupChannelAttachment> getGroupChannelAttachments() {
        return groupChannelAttachments;
    }

    public String getAvatar() {
        return avatar;
    }

    public ChannelInfo toInfo() {
        return new ChannelInfo(getId(), ChannelType.GROUP);
    }

    public GroupChannelDto toDto(StorageService storageService, @Nullable GroupChannelMessage lastMessage, int memberCount) {
        var lastUpdated = lastMessage == null ? getCreatedAt() : lastMessage.getCreatedAt();
        var lastMessageDto = lastMessage == null ? null : lastMessage.toDto(storageService);
        return new GroupChannelDto(
            getId(),
            ChannelType.GROUP,
            lastMessageDto,
            lastUpdated.toString(),
            getName(),
            getAvatar(),
            getDescription(),
            getCreatedAt().toString(),
            memberCount, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        var channel = (GroupChannel) o;

        return getId().equals(channel.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public static GroupChannel createTestGroupChannel() {
        var groupChannel = new GroupChannel();
        groupChannel.setName("test_group_channel_name");
        groupChannel.setDescription("test_group_channel_description");
        return groupChannel;
    }
}
