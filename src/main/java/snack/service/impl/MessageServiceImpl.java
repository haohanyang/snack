package snack.service.impl;

import snack.domain.storage.GroupChannelAttachment;
import snack.domain.storage.UserChannelAttachment;
import snack.domain.user.User;
import snack.service.StorageService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.util.Pair;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import snack.domain.message.GroupChannelMessage;
import snack.domain.message.UserChannelMessage;
import snack.repository.channel.GroupChannelMembershipRepository;
import snack.repository.channel.GroupChannelRepository;
import snack.repository.channel.UserChannelRepository;
import snack.repository.message.GroupChannelMessageRepository;
import snack.repository.message.UserChannelMessageRepository;
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
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

	private final UserRepository userRepository;
	private final UserChannelRepository userChannelRepository;
	private final GroupChannelRepository groupChannelRepository;
	private final UserChannelMessageRepository userMessageRepository;
	private final GroupChannelMessageRepository groupMessageRepository;
	private final GroupChannelMembershipRepository groupChannelMembershipRepository;
	private final GroupChannelAttachmentRepository groupChannelAttachmentRepository;
	private final UserChannelAttachmentRepository userChannelAttachmentRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;

	private final StorageService storageService;

	public MessageServiceImpl(UserRepository userRepository,
			UserChannelRepository userChannelRepository,
			GroupChannelRepository groupChannelRepository,
			UserChannelMessageRepository userMessageRepository,
			GroupChannelMessageRepository groupMessageRepository,
			GroupChannelMembershipRepository groupChannelMembershipRepository,
			GroupChannelAttachmentRepository groupChannelAttachmentRepository,
			UserChannelAttachmentRepository userChannelAttachmentRepository,
			SimpMessagingTemplate simpMessagingTemplate,
			StorageService storageService) {
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
	public Pair<MessageDto, List<User>> storeUserChannelMessage(MessageRequest request) throws Exception {
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

		var message = new UserChannelMessage(request.content(), author, channel);

		if (request.fileUploadResult() != null) {
			var fileInfo = storageService.getFileUploadResult(request.fileUploadResult(), authorId);
			var attachment = new UserChannelAttachment();
			attachment.setKey(fileInfo.key());
			attachment.setUploader(author);
			attachment.setName(fileInfo.fileName());
			attachment.setSize(fileInfo.size());
			attachment.setBucket(fileInfo.bucket());
			attachment.setContentType(fileInfo.ContentType());
			attachment.setChannel(channel);

			userChannelAttachmentRepository.save(attachment);
			message.setAttachment(attachment);
		}

		userMessageRepository.save(message);
		log.info("Saved user {}'s message {} to user channel {}", message.getAuthor().getId(), message.getId(),
				channelId);
		var messageDto = message.toDto(storageService);

		if (channel.getUser1().getId().equals(request.authorId())) {
			return Pair.of(messageDto, List.of(channel.getUser1(), channel.getUser2()));
		}
		return Pair.of(messageDto, List.of(channel.getUser2(), channel.getUser1()));
	}

	@Override
	@Transactional
	public Pair<MessageDto, List<User>> storeGroupChannelMessage(MessageRequest request)
			throws Exception {

		var authorId = request.authorId();
		var author = userRepository.findById(authorId)
				.orElseThrow(() -> new InvalidUserException(authorId));

		var channelId = request.channel().getId();
		var channel = groupChannelRepository.findById(channelId)
				.orElseThrow(() -> new InvalidChannelIdException(channelId));

		var memberStream = groupChannelMembershipRepository.findByChannel(channel)
				.stream()
				.map(e -> e.getMember());

		var memberIds = memberStream
				.map(e -> e.getId())
				.toList();

		if (!memberIds.contains(authorId)) {
			throw new IllegalArgumentException(
					"User " + author.getId() + " is not a member of the channel "
							+ channelId);
		}

		var message = new GroupChannelMessage(
				request.content(), author, channel);

		if (request.fileUploadResult() != null) {
			var fileInfo = storageService.getFileUploadResult(request.fileUploadResult(), authorId);
			var attachment = new GroupChannelAttachment();
			attachment.setKey(fileInfo.key());
			attachment.setUploader(author);
			attachment.setName(fileInfo.fileName());
			attachment.setSize(fileInfo.size());
			attachment.setBucket(fileInfo.bucket());
			attachment.setContentType(fileInfo.ContentType());
			attachment.setChannel(channel);

			groupChannelAttachmentRepository.save(attachment);
			message.setAttachment(attachment);
		}

		groupMessageRepository.save(message);
		var messageDto = message.toDto(storageService);

		var otherMembers = memberStream
				.filter(e -> !e.getId().equals(authorId))
				.toList();

		// Ensure that the author is the first member in the list
		var members = List.of(author);
		members.addAll(otherMembers);

		log.info("Saved user {}'s message {} to group channel {}", message.getAuthor().getId(), message.getId());
		return Pair.of(messageDto, members);
	}

	@Override
	@Transactional
	public List<MessageDto> getUserMessages(Integer channelId, @Nullable String requesterId)
			throws ChannelNotFoundException {
		var channel = userChannelRepository.findById(channelId)
				.orElseThrow(() -> new ChannelNotFoundException(channelId));
		// Check if the requester is a member of the channel
		if (requesterId != null) {
			if (!channel.getUser1().getId().equals(requesterId)
					&& !channel.getUser2().getId().equals(requesterId)) {
				throw new IllegalArgumentException(
						"User " + requesterId + " is not a member of the channel " + channelId);
			}
		}
		var messages = userMessageRepository.findByChannel(channel);
		return messages.stream()
				.map(e -> e.toDto(storageService)).toList();
	}

	@Override
	@Transactional
	public List<MessageDto> getGroupMessages(Integer channelId, @Nullable String requesterId)
			throws ChannelNotFoundException {
		var channel = groupChannelRepository.findById(channelId)
				.orElseThrow(() -> new ChannelNotFoundException(channelId));
		// Check if the requester is a member of the channel
		if (requesterId != null) {
			var membership = groupChannelMembershipRepository.findByMemberIdAndChannelId(requesterId,
					channelId);
			if (membership.isEmpty()) {
				throw new IllegalArgumentException(
						"User " + requesterId + " is not a member of the channel " + channelId);
			}
		}
		var messages = groupMessageRepository.findByChannel(channel);
		return messages.stream()
				.map(e -> e.toDto(storageService)).toList();
	}

	@Override
	@Async
	public void sendMessage(Collection<String> destinations, MessageDto message) {
		destinations.forEach(destination -> {
			simpMessagingTemplate.convertAndSend(destination, message);
			log.info("Sent message {} to {}", message.id(), destination);
		});
	}
}
