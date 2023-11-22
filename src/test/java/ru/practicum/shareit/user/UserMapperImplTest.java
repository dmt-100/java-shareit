package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

class UserMapperImplTest {

    private final UserMapperImpl userMapper = new UserMapperImpl();

    @Test
    @DisplayName("получен маппер в ДТО пользователя, когда вызван нуль, то получен нуль")
    void toUserDto() {
        UserDto userDto = userMapper.toUserDto(null);

        assertThat(userDto, nullValue());
    }

    @Test
    @DisplayName("получен маппер в пользователя, когда вызван нуль, то получен нуль")
    void toUser() {
        User user = userMapper.toUser(null);

        assertThat(user, nullValue());
    }

    @Test
    @DisplayName("получен маппер в список ДТО пользователей, когда вызван нуль, то получен нуль")
    void convertUserListToUserDTOList() {
        List<UserDto> users = userMapper.convertUserListToUserDTOList(null);

        assertThat(users, nullValue());
    }

}