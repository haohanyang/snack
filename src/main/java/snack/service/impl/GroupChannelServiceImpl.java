package snack.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import snack.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import snack.domain.channel.GroupChannel;
import snack.domain.channel.GroupChannelMembership;
import snack.domain.message.GroupChannelMessage;
import snack.repository.channel.GroupChannelMembershipRepository;
import snack.repository.channel.GroupChannelRepository;
import snack.repository.channel.UserChannelRepository;
import snack.repository.message.GroupMessageRepository;
import snack.repository.user.UserRepository;
import snack.service.GroupChannelService;
import snack.service.dto.GroupChannelDto;
import snack.service.dto.MembershipDto;
import snack.service.exception.ChannelNotFoundException;
import snack.service.exception.InvalidUserException;
import snack.service.exception.UserNotFoundException;
import snack.web.requests.GroupChannelRequest;

@Service
public class GroupChannelServiceImpl implements GroupChannelService {

    private final UserRepository userRepository;
    private final GroupChannelRepository groupChannelRepository;
    private final GroupChannelMembershipRepository groupChannelMembershipRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final StorageService storageService;

    public GroupChannelServiceImpl(UserRepository userRepository, UserChannelRepository userChannelRepository,
            GroupChannelRepository groupChannelRepository,
            GroupChannelMembershipRepository groupChannelMembershipRepository,
            GroupMessageRepository groupMessageRepository,
            StorageService storageService) {
        this.userRepository = userRepository;
        this.groupChannelRepository = groupChannelRepository;
        this.groupChannelMembershipRepository = groupChannelMembershipRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupChannelDto getChannel(Integer id) throws ChannelNotFoundException {
        var groupChannel = groupChannelRepository.findById(id)
                .orElseThrow(() -> new ChannelNotFoundException(id));
        var lastMessage = groupMessageRepository.findFirstByChannelOrderByCreatedAtDesc(groupChannel)
                .orElse(null);
        var memberIds = groupChannelMembershipRepository.getMemberIds(id);
        return groupChannel.toDto(storageService, lastMessage, memberIds.size());
    }

    @Override
    @Transactional
    public GroupChannelDto createChannel(GroupChannelRequest request)
            throws InvalidUserException {

        var creatorId = request.creatorId();
        var memberIds = new HashSet<>(request.memberIds());

        memberIds.remove(creatorId);

        var creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new InvalidUserException(request.creatorId()));
        var groupChannel = new GroupChannel(request.name(), request.description());
        groupChannelRepository.save(groupChannel);

        List<GroupChannelMembership> memberships = new LinkedList<>();
        memberships.add(new GroupChannelMembership(creator, groupChannel, true));
        for (var userId : memberIds) {
            var member = userRepository.findById(userId)
                    .orElseThrow(() -> new InvalidUserException(userId));
            var membership = new GroupChannelMembership(member, groupChannel, false);
            memberships.add(membership);
        }

        groupChannelMembershipRepository.saveAll(memberships);
        return groupChannel.toDto(storageService, null, memberships.size());
    }

    @Override
    public Collection<GroupChannelDto> getChannels(String userId, Boolean fetchLatestMessage)
            throws UserNotFoundException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Collection<GroupChannelDto> groupChannelDtos = new LinkedList<>();

        var channels = groupChannelMembershipRepository.findByMember(user).stream()
                .map(GroupChannelMembership::getChannel).toList();

        for (var channel : channels) {
            GroupChannelMessage lastMessage = null;
            if (fetchLatestMessage) {
                lastMessage = groupMessageRepository.findFirstByChannelOrderByCreatedAtDesc(channel)
                        .orElse(null);
            }
            var memberIds = groupChannelMembershipRepository.getMemberIds(channel.getId());
            groupChannelDtos.add(channel.toDto(storageService, lastMessage, memberIds.size()));
        }
        return groupChannelDtos;
    }

    @Override
    public Collection<MembershipDto> getMembers(Integer channelId)
            throws ChannelNotFoundException {
        var memberships = groupChannelMembershipRepository
                .findByChannel(groupChannelRepository.findById(channelId)
                        .orElseThrow(() -> new ChannelNotFoundException(channelId)));
        return memberships.stream().map(GroupChannelMembership::toDto).toList();
    }

}
