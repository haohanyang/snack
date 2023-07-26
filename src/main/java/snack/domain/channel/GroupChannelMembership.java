package snack.domain.channel;

import jakarta.persistence.*;

import java.sql.Timestamp;

import snack.domain.user.User;
import snack.service.dto.MembershipDto;

@Entity(name = "group_channel_membership")
@Table(name = "group_channel_memberships", schema = "app")
public class GroupChannelMembership {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_channel_membership_gen")
    @SequenceGenerator(name = "group_channel_membership_gen", sequenceName = "group_channel_membership_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private GroupChannel channel;

    @Column(name = "is_creator")
    private Boolean isCreator;

    public GroupChannelMembership() {
    }

    public GroupChannelMembership(User member, GroupChannel channel, Boolean isCreator) {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.member = member;
        this.channel = channel;
        this.isCreator = isCreator;
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public User getMember() {
        return member;
    }

    public Boolean getIsCreator() {
        return isCreator;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public GroupChannel getChannel() {
        return channel;
    }

    public void setChannel(GroupChannel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        GroupChannelMembership that = (GroupChannelMembership) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public MembershipDto toDto() {
        return new MembershipDto(
            this.id,
            this.member.toDto(),
            this.isCreator);
    }

}
