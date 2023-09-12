package snack.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import snack.service.MessageService;
import snack.service.dto.MessageDto;
import snack.web.requests.MessageRequest;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1")
public class MessageController {
    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
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

        var message = messageService.sendGroupChannelMessage(request);
        logger.info("User {} sent a message to group channel {}", message.author().id(), channelId);
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

        var message = messageService.sendUserChannelMessage(request);
        logger.info("User {} sent a message to user channel {}", message.author().id(), channelId);
        return message;
    }
}
