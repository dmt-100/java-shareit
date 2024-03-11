package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.UserControllerTest.createUser;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void getAllUsers() {
        User user = createUser();
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<User> result = userService.getAllUsers();
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(user.getId()));
    }

    @Test
    void getUserByIdWithValidData() {
        User user = createUser();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User result = userService.getUserById(user.getId());
        assertThat(result.getId(), equalTo(user.getId()));
    }

    @Test
    void getUserByIdWithoutUserEntity() {
        User user = createUser();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(user.getId()));
        assertThat(exception.getMessage(), equalTo("No such user"));
    }

    @Test
    void addUser() {
        User user = createUser();
        when(userRepository.save(any()))
                .thenReturn(user);
        User result = userService.addUser(user);
        assertThat(result.getId(), equalTo(user.getId()));
    }

    @Test
    void updateUserWithValidData() {
        User user = createUser();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User result = userService.updateUser(user.getId(), user);
        assertThat(user.getId(), equalTo(result.getId()));
    }

    @Test
    void updateUserWithoutUserEntity() {
        User user = createUser();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(user.getId(), user));
        assertThat(exception.getMessage(), equalTo("No such user"));
    }

    @Test
    void deleteUser() {
        User user = createUser();
        userService.addUser(user);
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}
