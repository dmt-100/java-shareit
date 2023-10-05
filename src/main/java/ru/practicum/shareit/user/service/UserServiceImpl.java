package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public UserDto getUserById(long userId) {
        return userRepository.getUserById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));
    }

    @Override
    public UserDto saveUser(User user) {
        return userRepository.saveUser(user).
                orElseThrow(() -> new UserNotFoundException("Пользователь не был создан: " + user));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        return userRepository.updateUser(userId, userDto).
                orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId +
                        " не был обновлён: " + userDto));
    }

    @Override
    public boolean deleteUserById(long userId) {
        return userRepository.deleteUserById(userId);
    }

}