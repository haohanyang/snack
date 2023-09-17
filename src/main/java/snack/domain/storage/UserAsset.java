package snack.domain.storage;

import snack.domain.user.User;
import snack.service.StorageService;
import snack.service.dto.AttachmentDto;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "user_asset")
@Table(name = "user_assets")
@Data
public class UserAsset {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "user_asset_gen", sequenceName = "user_asset_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_asset_gen")
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

    public AttachmentDto toDto(StorageService storageService) {
        return new AttachmentDto(getId(), storageService.getDownloadUrl(
                getKey()), getName(),
                getContentType());
    }
}
