package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequestDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;
import ru.practicum.utils.OffsetBasedPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.utils.Constant.PAGE_DEFAULT_FROM;
import static ru.practicum.utils.Constant.PAGE_DEFAULT_SIZE;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                        @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        Pageable page = new OffsetBasedPageRequest(from, size);
        return userService.getUsersByIds(ids, page)
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@Valid @RequestBody NewUserRequestDto newUserRequestDto) {
        User user = userService.saveUser(UserMapper.toUser(newUserRequestDto));
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@Positive @PathVariable Long userId) {
        userService.deleteById(userId);
    }
}