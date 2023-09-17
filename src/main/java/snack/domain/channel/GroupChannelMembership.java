package snack.domain.channel;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import snack.domain.user.User;
import snack.service.dto.MembershipDto;

@Entity(name = "group_channel_membership")
@Table(name = "group_channel_memberships")
@Data
@NoArgsConstructor
public class GroupChannelMembership {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "group_channel_membership_gen", sequenceName = "group_channel_membership_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_channel_membership_gen")
    private Integer id;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private GroupChannel channel;

    @Column(name = "is_creator")
    private Boolean isCreator;

    public GroupChannelMembership(User member, GroupChannel channel, Boolean isCreator) {
        this.member = member;
        this.channel = channel;
        this.isCreator = isCreator;
    }

    public MembershipDto toDto() {
        return new MembershipDto(
                this.id,
                this.member.toDto(false),
                this.isCreator);
    }
}
