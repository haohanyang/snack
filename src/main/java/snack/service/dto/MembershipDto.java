package snack.service.dto;

public record MembershipDto(
        Integer id,
        UserDto member,
        Boolean isCreator) {
}
