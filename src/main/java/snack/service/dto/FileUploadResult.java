package snack.service.dto;

public record FileUploadResult(
    String uri,
    String bucket,
    String key,
    String fileName,
    Long size,
    String ContentType) {
}
