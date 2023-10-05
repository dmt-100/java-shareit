package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    /**
     * Получение списка всех пользователей
     */
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        log.debug("Получен список пользователей, количество = {}", users.size());
        return users;
    }

    @GetMapping("/{userId}")
    /**
     * Получение пользователя по id
     */
    public UserDto getUserById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        log.debug("Получен пользователь с id = {}", userId);
        return user;
    }

    @PostMapping
    @Validated
    /**
     * Создание пользователя
     */
    public UserDto saveUser(@Valid @RequestBody User user) {
        UserDto userDto = userService.saveUser(user);
        log.debug("Добавлен новый пользователь: {}", userDto);
        // не пойму, почему лог не выводится в консоль =(
        return userDto;
    }

    @PatchMapping("/{userId}")
    /**
     * Редактирование пользователя
     */
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        userDto = userService.updateUser(userId, userDto);
        log.debug("Обновлен пользователь: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    /**
     * Удаление пользователя по id
     */
    public boolean deleteUserById(@PathVariable long userId) {
        log.debug("Удалён пользователь с id = {}", userId);
        return userService.deleteUserById(userId);
    }

}