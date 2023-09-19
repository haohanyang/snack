package snack.service.impl;

import snack.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import snack.domain.channel.UserChannel;
import snack.repository.channel.UserChannelRepository;
import snack.repository.message.UserChannelMessageRepository;
import snack.repository.user.UserRepository;
import snack.service.UserChannelService;
import snack.service.dto.UserChannelDto;
import snack.service.exception.ChannelNotFoundException;
import snack.service.exception.InvalidUserException;
import snack.service.exception.UserNotFoundException;
import snack.web.requests.UserChannelRequest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

@Service
public class UserChannelServiceImpl implements UserChannelService {

    private final UserRepository userRepository;
    private final UserChannelRepository userChannelRepository;
    private final UserChannelMessageRepository userMessageRepository;
    private final StorageService storageService;

    public UserChannelServiceImpl(UserRepository userRepository, UserChannelRepository userChannelRepository,
            UserChannelMessageRepository userMessageRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.userChannelRepository = userChannelRepository;
        this.userMessageRepository = userMessageRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public UserChannelDto createChannel(UserChannelRequest request)
            throws InvalidUserException {

        if (request.user1Id().equals(request.user2Id())) {
            throw new InvalidUserException("User ids are equal");
        }

        var user1 = userRepository.findById(request.user1Id())
                .orElseThrow(() -> new InvalidUserException(request.user1Id()));
        var user2 = userRepository.findById(request.user2Id())
                .orElseThrow(() -> new InvalidUserException(request.user2Id()));

        Optional<UserChannel> channel;

        if (user1.getId().compareTo(user2.getId()) < 0) {
            channel = userChannelRepository.findByUser1AndUser2(user1, user2);
        } else {
            channel = userChannelRepository.findByUser1AndUser2(user2, user1);
        }

        if (channel.isPresent()) {
            var lastMessage = userMessageRepository.findFirstByChannelOrderByCreatedAtDesc(channel.get())
                    .orElse(null);
            return channel.get().toDto(storageService, lastMessage);
        }

        var newChannel = new UserChannel(user1, user2);
        userChannelRepository.save(newChannel);
        return newChannel.toDto(storageService, null);
    }

    @Override
    @Transactional(readOnly = true)
    public UserChannelDto getChannel(Integer id) throws ChannelNotFoundException {
        var userChannel = userChannelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));
        var lastMessage = userMessageRepository.findFirstByChannelOrderByCreatedAtDesc(userChannel)
                .orElse(null);
        return userChannel.toDto(storageService, lastMessage);
    }

    /**
     * Get all user channels that the user is in
     *
     * @param userId The user's id
     * @return A list of channels that the user is in
     * @throws UserNotFoundException
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<UserChannelDto> getChannels(String userId) throws UserNotFoundException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        var channels = userChannelRepository.findByUser1OrUser2(user, user);
        var channelList = channels.stream().toList();

        Collection<UserChannelDto> dtos = new LinkedList<>();
        for (UserChannel channel : channelList) {
            var lastMessage = userMessageRepository.findFirstByChannelOrderByCreatedAtDesc(channel)
                    .orElse(null);
            dtos.add(channel.toDto(storageService, lastMessage));
        }
        return dtos;
    }

}
