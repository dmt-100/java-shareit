package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    /**
     * Получение списка всех пользователей
     */
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{userId}")
    /**
     * Получение пользователя по id
     */
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    @Validated
    /**
     * Создание пользователя
     */
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserDto userDto) {
        userDto = userService.saveUser(userDto);
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{userId}")
    /**
     * Редактирование пользователя
     */
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto = userService.updateUser(userId, userDto);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{userId}")
    /**
     * Удаление пользователя по id
     */
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

}