package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static ru.practicum.shareit.user.UserControllerTest.createDto;
import static ru.practicum.shareit.user.UserControllerTest.createUser;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserMapperTest {
    User user;
    UserDto dto;

    @Test
    void dtoToUser() {
        dto = createDto();
        user = UserMapper.dtoToUser(dto);
        assertNotNull(user);
        assertThat(user.getName(), equalTo(dto.getName()));
    }

    @Test
    void userToDto() {
        user = createUser();
        dto = UserMapper.userToDto(user);
        assertNotNull(dto);
        assertThat(user.getId(), equalTo(dto.getId()));
    }
}
