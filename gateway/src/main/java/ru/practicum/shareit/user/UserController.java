package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        log.info("Get user by id = {}", id);
        return userClient.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(UserDto.NewUser.class) @RequestBody UserDto user) {
        log.info("Add user = {}", user);
        return userClient.addUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Validated(UserDto.UpdateUser.class) @RequestBody UserDto user, @PathVariable long id) {
        log.info("Update user, userId = {}", id);
        return userClient.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        log.info("Delete user, id ={}", id);
        return userClient.deleteUser(id);
    }
}
