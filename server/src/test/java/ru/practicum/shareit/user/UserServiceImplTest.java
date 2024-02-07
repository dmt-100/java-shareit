package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotSaveException;
import ru.practicum.shareit.user.exception.UserNotUpdateException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("получены все пользователи, когда вызваны по умолчанию, то получен пустой список")
    void getAllUsers_whenInvokedDefault_thenReturnedEmptyList() {
        List<UserDto> expectedUsers = userService.getAllUsers();

        assertThat(expectedUsers, empty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("получены все пользователи, когда вызваны, то получен непустой список")
    void getAllUsers_whenInvoked_thenReturnedUsersCollectionInList() {
        List<User> expectedUsers = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsers = userService.getAllUsers();

        assertThat(UserMapper.INSTANCE.convertUserListToUserDTOList(expectedUsers),
                equalTo(actualUsers));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("получен пользователь по ид, когда пользователь найден, тогда он возвращается")
    void getUserById_whenUserFound_thenReturnedUser() {
        long userId = 0L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userService.getUserById(userId);

        assertThat(UserMapper.INSTANCE.toUserDto(expectedUser), equalTo(actualUser));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("получен пользователь по ид, когда пользователь не найден, " +
            "тогда выбрасывается исключение")
    void getUserById_whenUserNotFound_thenExceptionThrown() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));

        assertThat("Пользователь с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("сохранен пользователь, когда пользователь валиден, тогда он сохраняется")
    void saveUser_whenUserValid_thenSavedUser() {
        UserDto userToSave = new UserDto();
        when(userRepository.save(any(User.class))).thenReturn(UserMapper.INSTANCE.toUser(userToSave));

        UserDto actualUser = userService.saveUser(userToSave);

        assertThat(userToSave, equalTo(actualUser));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("сохранен пользователь, когда пользователь не валиден, тогда выбрасывается исключение")
    void saveUser_whenUserNotValid_thenExceptionThrown() {
        UserDto userToSave = new UserDto();
        when(userRepository.save(any(User.class)))
                .thenThrow(new UserNotSaveException("Пользователь не был создан"));

        final UserNotSaveException exception = assertThrows(UserNotSaveException.class,
                () -> userService.saveUser(userToSave));

        assertThat("Пользователь не был создан", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("обновлен пользователь, когда пользователь валиден, тогда он обновляется")
    void updateUser_whenUserFound_thenUpdatedUser() {
        Long userId = 0L;
        User oldUser = new User();
        oldUser.setName("1");
        oldUser.setEmail("1@mail.ru");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));

        User newUser = new User();
        newUser.setName("2");
        newUser.setEmail("1@mail.ru");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(newUser);

        UserDto actualUser = userService.updateUser(userId, UserMapper.INSTANCE.toUserDto(newUser));

        assertThat(newUser.getName(), equalTo(actualUser.getName()));
        assertThat(newUser.getEmail(), equalTo(actualUser.getEmail()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("обновлен пользователь, когда пользователь не валиден, " +
            "тогда выбрасывается исключение")
    void updateUser_whenUserNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User oldUser = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new UserNotUpdateException("Пользователь не был обновлен"));

        final UserNotUpdateException exception = assertThrows(UserNotUpdateException.class,
                () -> userService.updateUser(userId, new UserDto()));

        assertThat("Пользователь не был обновлен", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("обновлен пользователь, когда пользователь не найден, " +
            "тогда выбрасывается исключение")
    void updateUser_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotUpdateException exception = assertThrows(UserNotUpdateException.class,
                () -> userService.updateUser(userId, new UserDto()));

        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("удален пользователь, когда вызвано, тогда он удаляется")
    void deleteUser_whenInvoked_thenDeletedUser() {
        Long userId = 0L;

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

}