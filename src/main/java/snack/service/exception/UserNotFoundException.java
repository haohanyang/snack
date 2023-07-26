package snack.service.exception;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String userId) {
        super("UserId " + userId + " was not found");
    }
}
