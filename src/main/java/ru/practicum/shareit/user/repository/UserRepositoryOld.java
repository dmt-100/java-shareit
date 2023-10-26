package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryOld {

    List<UserDto> getAllUsers();

    Optional<User> getUserById(Long userId);

    Optional<UserDto> getUserDtoById(Long userId);

    Optional<UserDto> saveUser(UserDto userDto);

    Optional<UserDto> updateUser(Long userId, UserDto userDto);

    boolean deleteUserById(Long userId);

}