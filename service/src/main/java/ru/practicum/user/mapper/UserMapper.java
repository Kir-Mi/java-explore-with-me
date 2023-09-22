package ru.practicum.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@UtilityClass
public class UserMapper {
    public User toUser(NewUserRequestDto newUserRequestDto) {
        return User.builder()
                .name(newUserRequestDto.getName())
                .email(newUserRequestDto.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto userShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}