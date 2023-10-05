package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(Long userId);

    Optional<UserDto> saveUser(User user);

    Optional<UserDto> updateUser(long userId, UserDto userDto);

    boolean deleteUserById(long userId);

}