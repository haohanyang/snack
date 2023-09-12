package snack.service;

import java.util.Collection;

import snack.domain.user.User;
import snack.service.dto.UserDto;
import snack.service.exception.UserNotFoundException;
import snack.utils.Pair;
import snack.web.requests.UpdateProfileRequest;

public interface UserService {
    UserDto getUser(String userId) throws UserNotFoundException;

    /**
     * Creates a new user with the given user ID if user doesn't exist. The user ID must be unique and matches the route parameter.
     *
     * @param userId the user ID
     * @return a pair of user DTO and a boolean indicating whether the user was created or not
     * @throws Exception
     */
    Pair<UserDto, Boolean> createUser(User user) throws Exception;

    UserDto updateUser(String userId, UpdateProfileRequest request) throws Exception;

    Collection<UserDto> getFriends(String userId) throws UserNotFoundException;
}
