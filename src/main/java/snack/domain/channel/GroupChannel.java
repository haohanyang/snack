package snack.domain.channel;

import snack.domain.message.GroupChannelMessage;
import snack.domain.storage.GroupChannelAttachment;
import snack.service.StorageService;
import snack.service.dto.ChannelInfo;
import snack.service.dto.ChannelType;
import snack.service.dto.GroupChannelDto;
import snack.config.Constants;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "group_channel")
@Table(name = "group_channels")
@Data
@NoArgsConstructor
public class GroupChannel {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "group_channel_gen", sequenceName = "group_channel_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_channel_gen")
    private Integer id;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "avatar", length = 300)
    private String avatar = Constants.DEFAULT_GROUP_AVATAR;

    @Column(name = "group_name", length = 30, nullable = false)
    private String name;

    @OneToMany(mappedBy = "channel")
    private Set<GroupChannelMembership> groupChannelMemberships = new HashSet<>();

    @OneToMany(mappedBy = "channel")
    private Set<GroupChannelAttachment> groupChannelAttachments = new HashSet<>();

    public ChannelInfo toInfo() {
        return new ChannelInfo(getId(), ChannelType.GROUP);
    }

    public GroupChannel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public GroupChannelDto toDto(StorageService storageService, @Nullable GroupChannelMessage lastMessage,
            int memberCount) {
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

    // public static GroupChannel createTestGroupChannel() {
    // var groupChannel = new GroupChannel();
    // groupChannel.setName("test_group_channel_name");
    // groupChannel.setDescription("test_group_channel_description");
    // return groupChannel;
    // }
}
