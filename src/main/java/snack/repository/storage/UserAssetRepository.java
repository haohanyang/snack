package snack.repository.storage;

import org.springframework.data.repository.CrudRepository;

import snack.domain.storage.UserAsset;

public interface UserAssetRepository extends CrudRepository<UserAsset, Integer> {

}
