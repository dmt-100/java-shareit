package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto saveUser(User user);

    UserDto updateUser(long userId, UserDto userDto);

    boolean deleteUserById(long userId);

}