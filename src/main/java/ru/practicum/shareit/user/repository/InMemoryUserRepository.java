package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepositoryOld {

    Map<Long, User> users = new HashMap<>();
    public static long userId = 0;  // сквозной счетчик пользователей

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return users.values().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<UserDto> getUserDtoById(Long userId) {
        return users.values().stream()
                .filter(user -> user.getId().equals(userId))
                .map(UserMapper.INSTANCE::toUserDto)
                .findFirst();

    }

    @Override
    public Optional<UserDto> saveUser(UserDto userDto) {
        if (validateEmail(userDto.getEmail()) > 0) {
            return Optional.empty();
        }
        User user = UserMapper.INSTANCE.toUser(userDto);

        user.setId(getNextId());
        users.put(user.getId(), user);
        return Optional.of(UserMapper.INSTANCE.toUserDto(user));
    }

    @Override
    public Optional<UserDto> updateUser(Long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        if (validateIdAndEmail(userId, userDto.getEmail()) != 1) { // если изменяем адрес сам на себя
            if (validateEmail(userDto.getEmail()) > 0) {
                return Optional.empty();
            }
        }

        User user = users.get(userId);
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return Optional.of(UserMapper.INSTANCE.toUserDto(user));
    }

    @Override
    public boolean deleteUserById(Long userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
            return true;
        }
        return false;
    }

    private static Long getNextId() {
        return ++userId;
    }

    private long validateEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .count();
    }

    private long validateIdAndEmail(Long userId, String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> u.getId().equals(userId))
                .count();
    }

}