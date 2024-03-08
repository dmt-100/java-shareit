package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such user"));
    }

    @Transactional
    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(long id, User user) {
        user.setId(id);
        String name = user.getName();
        String email = user.getEmail();
        User userFromStorage = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such user"));
        if (name != null && !name.isBlank()) {
            userFromStorage.setName(name);
        }
        if (email != null && !email.isBlank()) {
            userFromStorage.setEmail(email);
        }
        return userFromStorage;
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
