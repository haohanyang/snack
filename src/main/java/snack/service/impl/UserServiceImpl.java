package snack.service.impl;

import snack.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import snack.repository.user.UserRepository;
import snack.service.UserService;
import snack.service.dto.UserDto;
import snack.service.exception.InvalidUserException;
import snack.service.exception.UserNotFoundException;
import snack.web.requests.UpdateProfileRequest;

import java.util.Collection;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;

    public UserServiceImpl(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserProfile(String userId, boolean ownProfile) throws UserNotFoundException {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        var dto = user.toDto(ownProfile);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getFriends(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return userRepository.findAll().stream().filter(u -> !Objects.equals(u.getId(), userId))
                .map(friend -> friend.toDto(false))
                .toList();
    }

    @Override
    @Transactional
    public UserDto updateUserProfile(String userId, UpdateProfileRequest request) throws Exception {
        var user = userRepository
                .findById(userId)
                .orElseThrow(() -> new InvalidUserException(userId));
        user.setFullName(request.fullName());
        user.setBio(request.bio());
        if (request.avatar() != null) {
            var verifiedUploadResult = storageService.getFileUploadResult(request.avatar(), userId);
            user.setAvatar(verifiedUploadResult.uri());
        }

        if (request.backgroundImage() != null) {
            var verifiedUploadResult = storageService.getFileUploadResult(request.backgroundImage(), userId);
            user.setBackgroundImage(verifiedUploadResult.uri());
        }

        var updatedUser = userRepository.save(user);
        return updatedUser.toDto(true);
    }
}
