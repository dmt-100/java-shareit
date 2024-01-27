package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {
        //given
        UserDto userDto = new UserDto();
        userDto.setEmail("akhraa1@yandex.ru");
        userDto.setId(1L);
        userDto.setName("Akhra");
        //when
        JsonContent<UserDto> result = json.write(userDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Akhra");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("akhraa1@yandex.ru");
    }
}