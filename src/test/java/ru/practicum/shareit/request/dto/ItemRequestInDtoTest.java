package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestInDtoTest {

    @Autowired
    private JacksonTester<ItemRequestInDto> json;

    @Test
    void testItemRequestInDto() throws Exception {
        //given
        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto();
        itemRequestInDto.setUserId(1L);
        itemRequestInDto.setDescription("нужна отвертка");
        itemRequestInDto.setCreated(createdTime);
        itemRequestInDto.setId(1L);
        //when
        JsonContent<ItemRequestInDto> result = json.write(itemRequestInDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("нужна отвертка");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}