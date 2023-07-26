package snack.service.dto;

public record AttachmentDto(
        Integer id,
        String url,
        ChannelInfo channel,
        String filename,
        String contentType) {
}
