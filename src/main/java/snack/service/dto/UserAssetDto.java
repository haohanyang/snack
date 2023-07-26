package snack.service.dto;

public record UserAssetDto(Integer id,
                           String bucket,
                           String key,
                           String name,
                           String contentType) {
}
