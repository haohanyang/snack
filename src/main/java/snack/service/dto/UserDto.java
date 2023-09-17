package snack.service.dto;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UserDto {

    @NonNull
    private final String id;

    @NonNull
    private final String fullName;

    @Nullable
    private String email;

    @NonNull
    private String avatar;

    @NonNull
    private String backgroundImage;

    @NonNull
    private String bio;
}
