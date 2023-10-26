package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Получение списка всех пользователей
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        log.info("Получен список пользователей, количество = {}", users.size());
        return ResponseEntity.ok().body(users);
    }

    /**
     * Получение пользователя по id
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Получен пользователь с id = {}", userId);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Создание пользователя
     */
    @PostMapping
    @Validated
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserDto userDto) {
        userDto = userService.saveUser(userDto);
        log.info("Добавлен новый пользователь: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Редактирование пользователя
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto = userService.updateUser(userId, userDto);
        log.info("Обновлен пользователь: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    /**
     * Удаление пользователя по id
     */
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Удалён пользователь с id = {}", userId);
        userService.deleteUserById(userId);
    }

}