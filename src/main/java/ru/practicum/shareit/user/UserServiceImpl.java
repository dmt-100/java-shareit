package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ContentAlreadyExistException;
import ru.practicum.shareit.exceptions.ContentNotFountException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (userDto.getId() == null) {
            throw new ContentNotFountException("Необходимо указать id пользователя");
        }
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ContentNotFountException("Пользователь не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        Optional<User> sameEmailUser = userRepository.findUserByEmail(user.getEmail());
        if (sameEmailUser.isPresent() && !sameEmailUser.get().getId().equals(user.getId())) {
            throw new ContentAlreadyExistException("Пользователь с таким email уже существует");
        }

        //Валидация User
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> results = validator.validate(user);
        if (!results.isEmpty()) {
            throw new ConstraintViolationException(results);
        }
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ContentNotFountException("Пользователя с id = " + id + " не существует"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new ContentNotFountException("Пользователя с id = " + id + " не существует"));
        userRepository.deleteById(id);
    }
}
