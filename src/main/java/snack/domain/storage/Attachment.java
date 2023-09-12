package snack.domain.storage;

import java.sql.Timestamp;

import snack.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import snack.service.StorageService;
import snack.service.dto.AttachmentDto;

@MappedSuperclass
public class Attachment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_gen")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @Column(name = "object_key", length = 36, unique = true, nullable = false)
    private String key;

    @Column(name = "file_name", length = 80, nullable = false)
    private String name;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "bucket", length = 30, nullable = false)
    private String bucket;

    @Column(name = "content_type", length = 30, nullable = false)
    private String contentType;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    public Attachment() {

    }

    public Attachment(String uuid, User uploader, String name, Long size, String bucket, String contentType) {
        this.key = uuid;
        this.uploader = uploader;
        this.name = name;
        this.size = size;
        this.bucket = bucket;
        this.contentType = contentType;
        this.createdAt = new Timestamp(System.currentTimeMillis());

    }

    public Integer getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public String getBucket() {
        return bucket;
    }

    public String getContentType() {
        return contentType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setKey(String uuid) {
        this.key = uuid;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public AttachmentDto toDto(StorageService storageService) {
        return new AttachmentDto(getId(), storageService.getDownloadUrl(getBucket(), getKey()), getName(), getContentType());
    }
}
