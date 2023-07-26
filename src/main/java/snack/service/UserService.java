package snack.service;

import java.util.Collection;

import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import snack.service.dto.UserDto;
import snack.service.exception.InvalidUserException;
import snack.service.exception.UserNotFoundException;
import snack.web.requests.UpdateProfileRequest;

public interface UserService {
    UserDto getUser(String userId) throws UserNotFoundException;

    OidcUserInfo mapUserInfo(OidcUserInfo info);

    UserDto updateUser(UpdateProfileRequest request) throws Exception;

    Collection<UserDto> getFriends(String userId) throws UserNotFoundException;
}
