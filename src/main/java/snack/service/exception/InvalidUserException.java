package snack.service.exception;

public class InvalidUserException extends IllegalArgumentException {   
 public InvalidUserException(String userId) {
        super("UserId " + userId + " does not exist");
    }
}
