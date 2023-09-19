package snack.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import snack.domain.channel.GroupChannelMembership;
import snack.domain.channel.UserChannel;
import snack.domain.message.FCMRegistrationToken;
import snack.domain.message.GroupChannelMessage;
import snack.domain.message.UserChannelMessage;
import snack.domain.storage.GroupChannelAttachment;
import snack.domain.storage.UserAsset;
import snack.domain.storage.UserChannelAttachment;
import snack.service.dto.UserDto;
import snack.config.Constants;

import lombok.Data;

import java.util.*;

@Entity(name = "user")
@Table(name = "users")
@Data
public class User {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @Email
    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "avatar", length = 300)
    private String avatar = Constants.DEFAULT_USER_AVATAR;

    @Column(name = "background_image", length = 300)
    private String backgroundImage;

    @Column(name = "bio", length = 200)
    private String bio;

    @OneToMany(mappedBy = "member")
    private Set<GroupChannelMembership> groupChannelMemberships = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<UserChannelMessage> userMessages = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private Set<GroupChannelMessage> groupMessages = new HashSet<>();

    @OneToMany(mappedBy = "user1")
    private Set<UserChannel> userChannels1 = new HashSet<>();

    @OneToMany(mappedBy = "user2")
    private Set<UserChannel> userChannels2 = new HashSet<>();

    @OneToMany(mappedBy = "uploader")
    private Set<GroupChannelAttachment> groupChannelAttachments = new HashSet<>();

    @OneToMany(mappedBy = "uploader")
    private Set<UserChannelAttachment> userChannelAttachments = new HashSet<>();

    @OneToMany(mappedBy = "uploader")
    private Set<UserAsset> userAssets = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<FCMRegistrationToken> fcmRegistrationTokens = new HashSet<>();

    public UserDto toDto(boolean ownProfile) {
        var user = new UserDto(
                getId(),
                getFullName(),
                getAvatar(),
                getBackgroundImage(),
                getBio());

        if (ownProfile) {
            user.setEmail(getEmail());
        }

        return user;
    }
}
