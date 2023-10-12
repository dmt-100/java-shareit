package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotSaveException;
import ru.practicum.shareit.user.exception.UserNotUpdateException;
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
    public UserDto getUserById(Long userId) {
        return userRepository.getUserDtoById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден."));
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        return userRepository.saveUser(userDto).orElseThrow(() ->
                new UserNotSaveException("Пользователь не был создан: " + userDto));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        return userRepository.updateUser(userId, userDto).orElseThrow(() ->
                new UserNotUpdateException("Пользователь с id = " + userId + " не был обновлён: " + userDto));
    }

    @Override
    public boolean deleteUserById(Long userId) {
        return userRepository.deleteUserById(userId);
    }

}