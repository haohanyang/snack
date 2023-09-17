package snack.service;

import java.util.Collection;

import snack.service.dto.UserDto;
import snack.web.requests.UpdateProfileRequest;

public interface UserService {
    /**
     * Returns the user profile with the given user ID.
     * 
     * @param userId     the user ID
     * @param ownProfile whether the requested profile is the user's own profile
     * @return the user profile
     * @throws Exception
     */
    UserDto getUserProfile(String userId, boolean ownProfile) throws Exception;

    /**
     * Updates the user profile with the given user ID.
     * 
     * @param userId
     * @param request
     * @return the updated user profile
     * @throws Exception
     */
    UserDto updateUserProfile(String userId, UpdateProfileRequest request) throws Exception;

    /**
     * Returns the user's friends.
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    Collection<UserDto> getFriends(String userId) throws Exception;
}
