package snack.service.dto;

public record UserDto(
        String id,
        String username,
        String firstName,
        String lastName,
        String avatar,
        String backgroundImage,
        String bio,
        String status) {
}
