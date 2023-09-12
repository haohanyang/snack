package snack.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import snack.domain.user.User;
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

    @GetMapping("users/{user_id}/profile")
    public UserDto getUser(@PathVariable(name = "user_id") String userId) throws Exception {
        var user = userService.getUser(userId);
        return user;
    }

    @PostMapping("users/{user_id}")
    public ResponseEntity<UserDto> createUser(@PathVariable(name = "user_id") String userId,
                                              @AuthenticationPrincipal Jwt principal) throws Exception {
        if (!userId.equals(principal.getSubject())) {
            throw new IllegalArgumentException("User Id didn't match");
        }
        var email = principal.getClaimAsString("email");
        var fullName = principal.getClaimAsString("name");
        var username = principal.getClaimAsString("preferred_username");

        var user = new User(userId, email, username, fullName);
        var result = userService.createUser(user);

        if (result.getSecond()) {
            // Return the user DTO with 201 status code
            return ResponseEntity.status(HttpStatus.CREATED).body(result.getFirst());
        } else {
            return ResponseEntity.ok(result.getFirst());
        }
    }

    @PatchMapping("users/{user_id}")
    public UserDto updateUser(@PathVariable(name = "user_id") String userId,
                              @RequestBody UpdateProfileRequest request,
                              @AuthenticationPrincipal Jwt principal
    ) throws Exception {
        if (!principal.getSubject().equals(userId)) {
            throw new IllegalArgumentException("User Id didn't match");
        }
        var user = userService.updateUser(userId, request);
        return user;
    }

    @GetMapping("users/{user_id}/friends")
    public Collection<UserDto> getFriends(@PathVariable(name = "user_id") String userId) {
        var users = userService.getFriends(userId);
        return users;
    }
}
