package snack.service.dto;

public record AttachmentDto(
    Integer id,
    String url,
    String filename,
    String contentType) {
}
