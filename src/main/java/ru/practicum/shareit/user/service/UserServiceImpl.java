package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotSaveException;
import ru.practicum.shareit.user.exception.UserNotUpdateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.INSTANCE.convertUserListToUserDTOList(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.INSTANCE.toUserDto(userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с идентификатором " + userId + " не найден."))));
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.INSTANCE.toUser(userDto));
            return UserMapper.INSTANCE.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserNotSaveException(String.format("Пользователь не был создан: " + userDto));
        }
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotUpdateException(String.format("Пользователь с id = " + userId + " не найден.")));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        try {
            return UserMapper.INSTANCE.toUserDto(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new UserNotUpdateException(String.format("Пользователь с id = " + userId + " не был обновлён: " + userDto));
        }
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

}