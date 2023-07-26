package snack.domain.storage;

import snack.domain.user.User;
import snack.service.dto.UserAssetDto;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity(name = "user_asset")
@Table(name = "user_assets", schema = "app")
@SequenceGenerator(name = "attachment_gen", sequenceName = "user_asset_seq", allocationSize = 1)
public class UserAsset extends Attachment {

    public UserAsset(String uuid, User uploader, String name, Long size, String bucket, String contentType) {
        super(uuid, uploader, name, size, bucket, contentType);
    }

    public UserAsset() {
        super();
    }
}
