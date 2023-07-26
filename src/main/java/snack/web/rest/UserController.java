package snack.web.rest;

import org.springframework.web.bind.annotation.*;

import snack.service.UserService;
import snack.service.dto.UserDto;
import snack.web.requests.UpdateProfileRequest;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/{user_id}")
    public UserDto getUser(@PathVariable(name = "user_id") String userId) {
        var user = userService.getUser(userId);
        return user;
    }

    @PatchMapping("users/{user_id}")
    public UserDto updateUser(@PathVariable(name = "user_id") String userId,
                              @RequestBody UpdateProfileRequest request) throws Exception {
        if (!userId.equals(request.userId()))
            throw new IllegalArgumentException("User ID in path and body do not match");
        var user = userService.updateUser(request);
        return user;
    }

    @GetMapping("users/{user_id}/friends")
    public Collection<UserDto> getFriends(@PathVariable(name = "user_id") String userId) {
        var users = userService.getFriends(userId);
        return users;
    }
}
