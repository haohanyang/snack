package snack.domain.user;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;
import snack.domain.channel.GroupChannelMembership;
import snack.domain.channel.UserChannel;
import snack.domain.message.GroupChannelMessage;
import snack.domain.message.UserChannelMessage;
import snack.domain.storage.GroupChannelAttachment;
import snack.domain.storage.UserAsset;
import snack.domain.storage.UserChannelAttachment;
import snack.service.dto.UserDto;
import jakarta.validation.constraints.Email;

import java.util.*;

@Entity(name = "user")
@Table(name = "users", schema = "app")
public class User {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @NaturalId
    @Column(name = "username", length = 30, nullable = false)
    private String username;

    @Column(name = "full_name", length = 50)
    private String fullName;

    @Email
    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "avatar", length = 300)
    private String avatar;

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

    public User() {
    }

    public User(String id, String email, String username, String fullName) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Set<GroupChannelAttachment> getGroupChannelAttachments() {
        return groupChannelAttachments;
    }

    public Set<UserAsset> getUserAssets() {
        return userAssets;
    }

    public Set<UserChannelAttachment> getUserChannelAttachments() {
        return userChannelAttachments;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<GroupChannelMessage> getGroupMessages() {
        return groupMessages;
    }

    public void setGroupMessages(Set<GroupChannelMessage> groupMessages) {
        this.groupMessages = groupMessages;
    }

    public Set<UserChannel> getUserChannels1() {
        return userChannels1;
    }

    public Set<UserChannel> getUserChannels2() {
        return userChannels2;
    }

    public Set<GroupChannelMembership> getGroupChannelMemberships() {
        return groupChannelMemberships;
    }

    public void setGroupChannelMemberships(Set<GroupChannelMembership> groupChannelMemberships) {
        this.groupChannelMemberships = groupChannelMemberships;
    }

    public Set<UserChannelMessage> getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(Set<UserChannelMessage> userMessages) {
        this.userMessages = userMessages;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String firstName) {
        this.fullName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatarUrl) {
        this.avatar = avatarUrl;
    }

    public void setUserChannels1(Set<UserChannel> userChannels1) {
        this.userChannels1 = userChannels1;
    }

    public void setUserChannels2(Set<UserChannel> userChannels2) {
        this.userChannels2 = userChannels2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static User createTestUser() {
        var user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(UUID.randomUUID().toString().substring(0, 15));
        user.setFullName("TestFirstName");
        user.setEmail("test@mail.com");
        return user;
    }

    public UserDto toDto() {
        // remove auth0| prefix
        return new UserDto(
            getId(),
            getUsername(),
            getFullName(),
            getAvatar(),
            getBackgroundImage(),
            getBio());
    }
}
