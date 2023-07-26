package snack.service.impl;

import snack.domain.channel.UserChannel;
import snack.domain.storage.GroupChannelAttachment;
import snack.domain.storage.UserChannelAttachment;
import snack.service.StorageService;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import snack.domain.message.GroupChannelMessage;
import snack.domain.message.UserChannelMessage;
import snack.repository.channel.GroupChannelMembershipRepository;
import snack.repository.channel.GroupChannelRepository;
import snack.repository.channel.UserChannelRepository;
import snack.repository.message.GroupMessageRepository;
import snack.repository.message.UserMessageRepository;
import snack.repository.storage.GroupChannelAttachmentRepository;
import snack.repository.storage.UserChannelAttachmentRepository;
import snack.repository.user.UserRepository;
import snack.service.MessageService;
import snack.service.dto.MessageDto;
import snack.service.exception.ChannelNotFoundException;
import snack.service.exception.InvalidChannelIdException;
import snack.service.exception.InvalidUserException;
import snack.web.requests.MessageRequest;

import java.util.Collection;

@Service
public class MessageServiceImpl implements MessageService {

    private final Logger logger = LoggerFactory.getLogger(snack.service.MessageService.class);
    private final UserRepository userRepository;
    private final UserChannelRepository userChannelRepository;
    private final GroupChannelRepository groupChannelRepository;
    private final UserMessageRepository userMessageRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final GroupChannelMembershipRepository groupChannelMembershipRepository;
    private final GroupChannelAttachmentRepository groupChannelAttachmentRepository;
    private final UserChannelAttachmentRepository userChannelAttachmentRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StorageService storageService;

    public MessageServiceImpl(UserRepository userRepository, UserChannelRepository userChannelRepository,
                              GroupChannelRepository groupChannelRepository, UserMessageRepository userMessageRepository,
                              GroupMessageRepository groupMessageRepository,
                              GroupChannelMembershipRepository groupChannelMembershipRepository,
                              GroupChannelAttachmentRepository groupChannelAttachmentRepository,
                              UserChannelAttachmentRepository userChannelAttachmentRepository,
                              SimpMessagingTemplate simpMessagingTemplate, StorageService storageService) {
        this.userRepository = userRepository;
        this.userChannelRepository = userChannelRepository;
        this.groupChannelRepository = groupChannelRepository;
        this.userMessageRepository = userMessageRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.groupChannelMembershipRepository = groupChannelMembershipRepository;
        this.groupChannelAttachmentRepository = groupChannelAttachmentRepository;
        this.userChannelAttachmentRepository = userChannelAttachmentRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public MessageDto sendUserChannelMessage(MessageRequest request) throws Exception {
        var authorId = request.authorId();
        var author = userRepository.findById(authorId)
            .orElseThrow(() -> new InvalidUserException(authorId));

        var channelId = request.channel().getId();
        var channel = userChannelRepository.findById(channelId)
            .orElseThrow(() -> new InvalidChannelIdException(channelId));
        if (!channel.getUser1().equals(author) && !channel.getUser2().equals(author)) {
            throw new IllegalArgumentException(
                "User " + author.getId() + " is not a member of the channel " + channelId);
        }
        var message = new UserChannelMessage(author, request.content(), channel);

        if (request.fileUploadResult() != null) {
            var fileInfo = storageService.getFileUploadResult(request.fileUploadResult(), authorId);
            var attachment = userChannelAttachmentRepository.save(new UserChannelAttachment(
                fileInfo.key(),
                author,
                fileInfo.fileName(),
                fileInfo.size(),
                fileInfo.bucket(),
                fileInfo.ContentType(),
                channel
            ));
            message.setAttachment(attachment);
        }

        userMessageRepository.save(message);
        var messageDto = message.toDto(storageService);
        simpMessagingTemplate.convertAndSend("/gateway/" + channel.getUser1().getId(), messageDto);
        simpMessagingTemplate.convertAndSend("/gateway/" + channel.getUser2().getId(), messageDto);
        return messageDto;
    }

    @Override
    @Transactional
    public MessageDto sendGroupChannelMessage(MessageRequest request)
        throws Exception {

        var authorId = request.authorId();
        var author = userRepository.findById(authorId)
            .orElseThrow(() -> new InvalidUserException(authorId));

        var channelId = request.channel().getId();
        var channel = groupChannelRepository.findById(channelId)
            .orElseThrow(() -> new InvalidChannelIdException(channelId));

        var memberIds = groupChannelMembershipRepository.getMemberIds(channelId);
        if (!memberIds.contains(authorId)) {
            throw new IllegalArgumentException(
                "User " + author.getUsername() + " is not a member of the channel "
                    + channelId);
        }

        var message = new GroupChannelMessage(author, request.content(), channel);

        if (request.fileUploadResult() != null) {
            var fileInfo = storageService.getFileUploadResult(request.fileUploadResult(), authorId);
            var attachment = groupChannelAttachmentRepository.save(new GroupChannelAttachment(
                fileInfo.key(),
                author,
                fileInfo.fileName(),
                fileInfo.size(),
                fileInfo.bucket(),
                fileInfo.ContentType(),
                channel
            ));
            message.setAttachment(attachment);
        }

        groupMessageRepository.save(message);
        var messageDto = message.toDto(storageService);

        for (var memberId : memberIds) {
            simpMessagingTemplate.convertAndSend("/gateway/" + memberId, messageDto);
        }
        return messageDto;
    }

    @Override
    @Transactional
    public Collection<MessageDto> getUserMessages(Integer channelId, @Nullable String requesterId) throws ChannelNotFoundException {
        var channel = userChannelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
        // Check if the requester is a member of the channel
        if (requesterId != null) {
            if (!channel.getUser1().getId().equals(requesterId) && !channel.getUser2().getId().equals(requesterId)) {
                throw new IllegalArgumentException("User " + requesterId + " is not a member of the channel " + channelId);
            }
        }
        var messages = userMessageRepository.findByChannel(channel);
        return messages.stream()
            .map(e -> e.toDto(storageService)).toList();
    }

    @Override
    @Transactional
    public Collection<MessageDto> getGroupMessages(Integer channelId, @Nullable String requesterId) throws ChannelNotFoundException {
        var channel = groupChannelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException(channelId));
        // Check if the requester is a member of the channel
        if (requesterId != null) {
            var membership = groupChannelMembershipRepository.findByMemberIdAndChannelId(requesterId, channelId);
            if (membership.isEmpty()) {
                throw new IllegalArgumentException("User " + requesterId + " is not a member of the channel " + channelId);
            }
        }
        var messages = groupMessageRepository.findByChannel(channel);
        return messages.stream()
            .map(e -> e.toDto(storageService)).toList();
    }
}
