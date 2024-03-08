package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllerTest {
    ObjectMapper mapper;
    MockMvc mvc;

    @MockBean
    UserService userService;

    static User createUser() {
        return User.builder()
                .id(1)
                .name("Bob")
                .email("user@user.com")
                .build();
    }

    static UserDto createDto() {
        return UserDto.builder()
                .id(1)
                .name("Bob")
                .email("user@user.com")
                .build();
    }

    @Test
    @SneakyThrows
    void addUser() {
        UserDto dto = createDto();
        User user = createUser();
        when(userService.addUser(any()))
                .thenReturn(user);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.name").value(user.getName())
                );
    }

    @Test
    @SneakyThrows
    void updateUser() {
        UserDto dto = createDto();
        User user = createUser();
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(user);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(patch("/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.name").value(user.getName())
                );
    }

    @Test
    void deleteUser() {
        User user = createUser();
        userService.addUser(user);
        userService.deleteUser(user.getId());
        verify(userService, times(1)).deleteUser(user.getId());
    }

    @Test
    @SneakyThrows
    void getUserById() {
        User user = createUser();
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.name").value(user.getName())
                );
    }

    @Test
    @SneakyThrows
    void getAllUsers() {
        User user = createUser();
        when(userService.getAllUsers())
                .thenReturn(List.of(user));
        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].id").value(user.getId()),
                        jsonPath("$[0].name").value(user.getName())
                );
    }
}
