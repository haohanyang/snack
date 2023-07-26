package snack.service.impl;

import snack.service.StorageService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import snack.domain.user.User;
import snack.repository.user.UserRepository;
import snack.service.UserService;
import snack.service.dto.UserDto;
import snack.service.exception.InvalidUserException;
import snack.service.exception.UserNotFoundException;
import snack.web.requests.UpdateProfileRequest;

import java.util.Collection;
import java.util.HashMap;
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
    public UserDto getUser(String userId) throws UserNotFoundException {
        return userRepository.findById(userId).map(User::toDto)
            .orElseThrow(() -> new UserNotFoundException("User " + userId + " was not found"));
    }

    @Transactional(readOnly = true)
    public Collection<UserDto> getFriends(String userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return userRepository.findAll().stream().filter(u -> !Objects.equals(u.getId(), userId)).map(User::toDto).toList();
    }

    @Override
    public UserDto updateUser(UpdateProfileRequest request) throws Exception {
        var user = userRepository.findById(request.userId())
            .orElseThrow(() -> new InvalidUserException(request.userId()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setBio(request.bio());
        user.setStatus(request.status());
        if (request.avatar() != null) {
            var verifiedUploadResult = storageService.getFileUploadResult(request.avatar(), request.userId());
            user.setAvatar(verifiedUploadResult.uri());
        }

        if (request.backgroundImage() != null) {
            var verifiedUploadResult = storageService.getFileUploadResult(request.backgroundImage(), request.userId());
            user.setBackgroundImage(verifiedUploadResult.uri());
        }

        var updatedUser = userRepository.save(user);
        return updatedUser.toDto();
    }

    @Override
    @Transactional
    public OidcUserInfo mapUserInfo(OidcUserInfo info) {
        var id = info.getSubject();
        var user = userRepository.findById(id).orElseGet(() -> {
            var newUser = new User();
            newUser.setId(id);
            newUser.setUsername(info.getClaim("username"));
            newUser.setEmail(info.getEmail());
            newUser.setFirstName(info.getGivenName());
            newUser.setLastName(info.getFamilyName());
            return userRepository.save(newUser);
        });

        var claims = new HashMap<>(info.getClaims());
        claims.put("given_name", user.getFirstName());
        claims.put("family_name", user.getLastName());
        claims.put("name", user.getFirstName() + " " + user.getLastName());
        claims.put("picture", user.getAvatar());
        return new OidcUserInfo(claims);
    }
}
