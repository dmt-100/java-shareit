package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validations.Create;
import ru.practicum.shareit.validations.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Возвращен список всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Возвращен пользователь с id = {}", id);
        return userService.getUserById(id);
    }

    //@Validated(value = Create.class)
    @PostMapping
    public UserDto saveUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        UserDto savedUserDto = userService.saveUser(userDto);
        log.info("Пользователь сохранен, id = {}", savedUserDto.getId());
        return savedUserDto;
    }

    //@Validated(value = Update.class)
    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated(Update.class) @RequestBody UserDto userDto,
                              @PathVariable Long userId) {
        userDto.setId(userId);
        UserDto savedUserDto = userService.updateUser(userDto);
        log.info("Обновлены поля у пользователя с id {}", savedUserDto.getId());
        return userService.updateUser(savedUserDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("Пользователь удален, id = {}", id);
    }
}
