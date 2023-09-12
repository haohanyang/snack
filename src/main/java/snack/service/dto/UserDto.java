package snack.service.dto;

public record UserDto(
    String id,
    String username,
    String fullName,
    String avatar,
    String backgroundImage,
    String bio) {
}
