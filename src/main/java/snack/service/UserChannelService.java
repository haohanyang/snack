package snack.service;

import java.util.Collection;

import snack.service.dto.UserChannelDto;
import snack.service.exception.ChannelNotFoundException;
import snack.service.exception.InvalidUserException;
import snack.service.exception.UserNotFoundException;
import snack.web.requests.UserChannelRequest;

public interface UserChannelService {
    /**
     * Create a channel between two users. If the channel already exists, return the
     * existing channel.
     *
     * @param request channel data
     * @return The created or existing channel
     * @throws IllegalArgumentException if any user does not exist or the two users
     *                                  are the same
     */
    UserChannelDto createChannel(UserChannelRequest request)
            throws InvalidUserException;

    /**
     * Get the channel by id
     *
     * @param id
     * @return The channel
     * @throws ChannelNotFoundException
     */
    UserChannelDto getChannel(Integer id) throws ChannelNotFoundException;

    /**
     * Get all user channels that the user is in
     *
     * @param userId The user's id
     * @return A list of channels that the user is in
     * @throws UserNotFoundException if the user does not exist
     */
    Collection<UserChannelDto> getChannels(String userId) throws UserNotFoundException;

}
