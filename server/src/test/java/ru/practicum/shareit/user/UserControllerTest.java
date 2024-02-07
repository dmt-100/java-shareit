package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("получены все пользователи, когда вызваны по умолчанию, то ответ статус ок и пустое тело")
    void getAllUsers_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(response.getBody(), empty());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("получены все пользователи, когда вызваны, то ответ статус ок и непустое тело")
    void getAllUsers_whenInvoked_thenResponseStatusOkWithUsersCollectionInBody() {
        List<UserDto> expectedUsers = List.of(new UserDto());
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedUsers, equalTo(response.getBody()));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("получен пользователь по ид, когда пользователь найден, " +
            "то ответ статус ок, и он возвращается")
    void getUserById_whenUserFound_thenReturnedUser() {
        long userId = 0L;
        UserDto expectedUser = new UserDto();
        when(userService.getUserById(userId)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.getUserById(userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedUser, equalTo(response.getBody()));
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("сохранен пользователь, когда пользователь валиден, " +
            "то ответ статус ок, и он сохраняется")
    void saveUser_whenUserValid_thenSavedUser() {
        UserDto expectedUser = new UserDto();
        when(userService.saveUser(expectedUser)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.saveUser(expectedUser);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedUser, equalTo(response.getBody()));
        verify(userService, times(1)).saveUser(expectedUser);
    }

    @Test
    @DisplayName("обновлен пользователь, когда пользователь валиден, " +
            "то ответ статус ок, и он обновляется")
    void updateUser_whenUserValid_thenUpdatedUser() {
        Long userId = 0L;
        UserDto newUser = new UserDto();
        newUser.setName("2");
        newUser.setEmail("2@mail.ru");
        when(userService.updateUser(userId, newUser)).thenReturn(newUser);

        ResponseEntity<UserDto> response = userController.updateUser(userId, newUser);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(newUser, equalTo(response.getBody()));
        verify(userService, times(1)).updateUser(userId, newUser);
    }

}