package snack.service;

import java.util.Collection;

import snack.service.dto.GroupChannelDto;
import snack.service.dto.MembershipDto;
import snack.service.exception.ChannelNotFoundException;
import snack.service.exception.InvalidUserException;
import snack.service.exception.UserNotFoundException;
import snack.web.requests.GroupChannelRequest;

public interface GroupChannelService {
        /**
         * Get a group channel by id
         *
         * @param id channel id
         * @return The group channel
         * @throws ChannelNotFoundException if the channel does not exist
         */
        GroupChannelDto getChannel(Integer id) throws ChannelNotFoundException;

        /**
         * Create a group channel
         *
         * @param request channel data
         * @return The created channel
         * @throws InvalidUserException if any user related does not exist
         */
        GroupChannelDto createChannel(GroupChannelRequest request)
                        throws InvalidUserException;

        /**
         * Get all group channels the user is a member of
         *
         * @param userId             user id
         * @param fetchLatestMessage whether to fetch the latest message of each channel
         * @return A list of group channels
         * @throws UserNotFoundException if the user does not exist
         */
        Collection<GroupChannelDto> getChannels(String userId, Boolean fetchLatestMessage) throws UserNotFoundException;

        /**
         * Get all members of a group channel
         *
         * @param channelId channel id
         * @return A list of users
         * @throws ChannelNotFoundException if the channel does not exist
         */
        Collection<MembershipDto> getMembers(Integer channelId)
                        throws ChannelNotFoundException;

}
