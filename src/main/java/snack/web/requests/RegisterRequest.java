package snack.web.requests;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        String avatar,
        String backgroundImage,
        String bio,
        String status) {
}
