package snack.web.rest;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import snack.service.GroupChannelService;
import snack.service.UserChannelService;
import snack.service.dto.*;
import snack.web.requests.GroupChannelRequest;
import snack.web.requests.UserChannelRequest;

@RestController
@RequestMapping("/api/v1")
public class ChannelController {
    private final Logger logger = LoggerFactory.getLogger(ChannelController.class);
    private final GroupChannelService groupChannelService;
    private final UserChannelService userChannelService;

    public ChannelController(
        GroupChannelService groupChannelService,
        UserChannelService userChannelService) {
        this.groupChannelService = groupChannelService;
        this.userChannelService = userChannelService;
    }

    /**
     * Get all channels of a user
     *
     * @param userId the user ID
     * @return a collection of user channels and group channels
     */
    @GetMapping("users/{user_id}/channels")
    public Channels getChannels(@PathVariable(name = "user_id") String userId, @AuthenticationPrincipal Jwt principal) {
        if (!userId.equals(principal.getSubject())) {
            throw new IllegalArgumentException("Principal ID didn't match the requested user ID");
        }
        var userChannels = userChannelService.getChannels(userId);
        var groupChannels = groupChannelService.getChannels(userId, true);

        return new Channels(
            userChannels,
            groupChannels);
    }

    /* Group Channels */

    /**
     * Get all group channels the user is in
     *
     * @param userId the user ID
     * @return a collection of group channels
     */
    @GetMapping("users/{user_id}/channels/group")
    public Collection<GroupChannelDto> getGroupChannels(@PathVariable(name = "user_id") String userId, @AuthenticationPrincipal Jwt principal) {
        if (!userId.equals(principal.getSubject())) {
            throw new IllegalArgumentException("Principal ID didn't match the requested user ID");
        }
        var groupChannels = groupChannelService.getChannels(userId, false);
        return groupChannels;
    }

    /**
     * Create a group channel
     *
     * @param request the request body
     * @return the created group channel
     */
    @PostMapping("channels/group")
    @ResponseStatus(code = HttpStatus.CREATED)
    public GroupChannelDto createGroupChannel(
        @RequestBody GroupChannelRequest request, @AuthenticationPrincipal Jwt principal) {
        if (!request.creatorId().equals(principal.getSubject())) {
            throw new IllegalArgumentException("Principal ID didn't match the creator ID");
        }
        var channel = groupChannelService.createChannel(request);
        logger.info("User {} created a group channel {}, name {}", request.creatorId(), channel.id(), channel.name());
        return channel;
    }

    /**
     * Get a group channel by ID
     *
     * @param channelId the channel ID
     * @return the group channel
     */
    @GetMapping("channels/group/{channel_id}")
    public GroupChannelDto getGroupChannel(@PathVariable(name = "channel_id") Integer channelId) {
        return groupChannelService.getChannel(channelId);
    }

    /**
     * Get all members of a group channel
     *
     * @param channelId the channel ID
     * @return a collection of group members
     */
    @GetMapping("channels/group/{channel_id}/members")
    public Collection<MembershipDto> getGroupChannelMembers(@PathVariable(name = "channel_id") Integer channelId) {
        return groupChannelService.getMembers(channelId);
    }

    /* User Channels */

    /**
     * Create a user channel
     *
     * @return the created user channel
     */
    @PostMapping("channels/user")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserChannelDto createUserChannel(
        @RequestBody UserChannelRequest request, @AuthenticationPrincipal Jwt principal) {
        if (!request.user1Id().equals(principal.getSubject()) && !request.user2Id().equals(principal.getSubject())) {
            throw new IllegalArgumentException("Principal ID didn't match any user ID");
        }
        var channel = userChannelService.createChannel(request);
        logger.info("User channel of user {} and user {} was created, channel id {}", channel.user1().id(),
            channel.user2().id(), channel.id());
        return channel;
    }

    /**
     * Get a user channel by ID
     *
     * @param channelId the channel ID
     * @return the user channel
     */
    @GetMapping("channels/user/{channel_id}")
    public UserChannelDto getUserChannel(@PathVariable(name = "channel_id") Integer channelId) {
        return userChannelService.getChannel(channelId);
    }
}
