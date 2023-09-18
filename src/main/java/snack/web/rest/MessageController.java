package snack.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import snack.service.MessageService;
import snack.service.NotificationService;
import snack.service.dto.MessageDto;
import snack.service.dto.ResponseMessage;
import snack.web.requests.MessageRequest;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class MessageController {

    private final MessageService messageService;
    private final NotificationService notificationService;

    public MessageController(MessageService messageService, NotificationService notificationService) {
        this.messageService = messageService;
        this.notificationService = notificationService;
    }

    @GetMapping("channels/group/{channel_id}/messages")
    public Collection<MessageDto> getGroupChannelMessages(@PathVariable(name = "channel_id") Integer channelId,
            @AuthenticationPrincipal Jwt principal) {
        return messageService.getGroupMessages(channelId, principal.getSubject());
    }

    @GetMapping("channels/user/{channel_id}/messages")
    public Collection<MessageDto> getUserChannelMessages(@PathVariable(name = "channel_id") Integer channelId,
            @AuthenticationPrincipal Jwt principal) {
        return messageService.getUserMessages(channelId, principal.getSubject());
    }

    @PostMapping("channels/group/{channel_id}/messages")
    @ResponseStatus(code = HttpStatus.CREATED)
    public MessageDto sendGroupChannelMessage(@PathVariable(name = "channel_id") Integer channelId,
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal Jwt principal)
            throws Exception {
        if (!channelId.equals(request.channel().getId())) {
            throw new IllegalArgumentException("Channel ID in the path didn't match the channel ID in the request");
        }

        if (!principal.getSubject().equals(request.authorId())) {
            throw new IllegalArgumentException("Principal ID didn't match the author ID in the request");
        }

        var result = messageService.storeGroupChannelMessage(request);
        var message = result.getFirst();
        var receivers = result.getSecond();

        // Send messages via websocket
        messageService.sendMessage(
                receivers.stream().map(user -> "/gateway/" + user.getId()).toList(), message);
        // Send notification to receivers other than the first user
        notificationService.sendMessageNotification(receivers.subList(1, receivers.size()), message);
        return message;
    }

    @PostMapping("channels/user/{channel_id}/messages")
    @ResponseStatus(code = HttpStatus.CREATED)
    public MessageDto sendUserChannelMessage(@PathVariable(name = "channel_id") Integer channelId,
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal Jwt principal)
            throws Exception {
        if (!channelId.equals(request.channel().getId())) {
            throw new IllegalArgumentException("The channel ID in the path didn't match the channel ID in the request");
        }

        if (!principal.getSubject().equals(request.authorId())) {
            throw new IllegalArgumentException("The principal ID didn't match the author ID in the request");
        }

        var result = messageService.storeUserChannelMessage(request);
        var message = result.getFirst();
        var receivers = result.getSecond();
        // Send messages via websocket
        messageService.sendMessage(
                receivers.stream().map(user -> "/gateway/" + user.getId()).toList(), message);
        // Send notification to receivers other than the first user
        notificationService.sendMessageNotification(receivers.subList(1, receivers.size()), message);
        return message;
    }

    @PostMapping("users/{user_id}/fcm_tokens")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseMessage storeFCMRegistrationToken(@PathVariable(name = "user_id") String userId,
            @RequestBody StoreFCMRegistrationTokenRequest request,
            @AuthenticationPrincipal Jwt principal) throws Exception {
        if (Objects.equals(userId, "@me")) {
            userId = principal.getSubject();
        }

        if (!Objects.equals(userId, principal.getSubject())) {
            throw new IllegalArgumentException("The principal ID didn't match the user ID in the request");
        }

        var token = request.token();
        notificationService.storeFCMRegistrationToken(userId, token);
        return new ResponseMessage("token stored");
    }

    static record StoreFCMRegistrationTokenRequest(String token) {
    }
}
