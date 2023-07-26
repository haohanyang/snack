package snack.service.exception;

public class ChannelNotFoundException extends NotFoundException {
    public ChannelNotFoundException(Integer channelId) {
        super("Channel " + channelId + " does not exist");
    }

    public ChannelNotFoundException(String username1, String username2) {
        super("User channel between " + username1 + " and " + username2 + " does not exist");
    }

}
