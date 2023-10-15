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

    @GetMapping
    /**
     * Получение списка всех пользователей
     */
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        log.info("Получен список пользователей, количество = {}", users.size());
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{userId}")
    /**
     * Получение пользователя по id
     */
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Получен пользователь с id = {}", userId);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    @Validated
    /**
     * Создание пользователя
     */
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserDto userDto) {
        userDto = userService.saveUser(userDto);
        log.info("Добавлен новый пользователь: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{userId}")
    /**
     * Редактирование пользователя
     */
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto = userService.updateUser(userId, userDto);
        log.info("Обновлен пользователь: {}", userDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    /**
     * Удаление пользователя по id
     */
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Удалён пользователь с id = {}", userId);
        userService.deleteUserById(userId);
    }

}