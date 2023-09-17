package snack.web.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import snack.service.UserService;
import snack.service.dto.UserDto;
import snack.web.requests.UpdateProfileRequest;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/{user_id}/profile")
    public UserDto getUserProfile(
            @PathVariable(name = "user_id") String userId,
            @AuthenticationPrincipal Jwt principal) throws Exception {
        if (Objects.equals(userId, "@me")) {
            userId = principal.getSubject();
        }
        var user = userService.getUserProfile(userId, Objects.equals(userId, principal.getSubject()));
        return user;
    }

    @PatchMapping("users/{user_id}/profile")
    public UserDto updateUser(@PathVariable(name = "user_id") String userId,
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt principal) throws Exception {
        if (Objects.equals(userId, "@me")) {
            userId = principal.getSubject();
        }

        if (!Objects.equals(userId, principal.getSubject())) {
            throw new IllegalArgumentException("User Id didn't match");
        }

        var user = userService.updateUserProfile(userId, request);
        return user;
    }

    @GetMapping("users/{user_id}/friends")
    public Collection<UserDto> getFriends(
            @PathVariable(name = "user_id") String userId,
            @AuthenticationPrincipal Jwt principal) throws Exception {
        if (Objects.equals(userId, "@me")) {
            userId = principal.getSubject();
        }
        var friends = userService.getFriends(userId);
        return friends;
    }
}
