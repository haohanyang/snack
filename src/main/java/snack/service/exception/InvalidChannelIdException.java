package snack.service.exception;

public class InvalidChannelIdException extends IllegalArgumentException {
    public InvalidChannelIdException(Integer channelId) {
        super("Channel " + channelId + " does not exist");
    }

    public InvalidChannelIdException(String username1, String username2) {
        super("User channel between " + username1 + " and " + username2 + " does not exist");
    }
}
