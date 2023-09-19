package snack.domain.storage;

import snack.domain.channel.GroupChannel;
import snack.domain.user.User;
import snack.service.StorageService;
import snack.service.dto.AttachmentDto;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "group_channel_attachment")
@Table(name = "group_channel_attachments")
@Data
public class GroupChannelAttachment {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "group_channel_attachment_gen", sequenceName = "group_channel_attachment_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_channel_attachment_gen")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @Column(name = "object_key", length = 50, unique = true, nullable = false)
    private String key;

    @Column(name = "file_name", length = 100, nullable = false)
    private String name;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "bucket", length = 30, nullable = false)
    private String bucket;

    @Column(name = "content_type", length = 30, nullable = false)
    private String contentType;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id")
    private GroupChannel channel;

    // public GroupChannelAttachment(String key, User user, String name, Long size,
    // String bucket, String contentType,
    // GroupChannel channel) {

    // this.key = key;
    // this.uploader = user;
    // this.name = name;
    // this.size = size;
    // this.bucket = bucket;
    // this.contentType = contentType;
    // this.channel = channel;
    // }

    public AttachmentDto toDto(StorageService storageService) {
        return new AttachmentDto(getId(), storageService.getDownloadUrl(
                getKey()), getName(),
                getContentType());
    }
}
