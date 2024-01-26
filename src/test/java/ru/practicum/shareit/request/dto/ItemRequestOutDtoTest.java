package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestOutDtoTest {

    @Autowired
    private JacksonTester<ItemRequestOutDto> json;

    @Test
    void testItemRequestOutDto() throws Exception {
        //given
        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto();
        itemRequestOutDto.setUserId(1L);
        itemRequestOutDto.setDescription("нужна отвертка");
        itemRequestOutDto.setCreated(createdTime);
        itemRequestOutDto.setId(1L);
        //when
        JsonContent<ItemRequestOutDto> result = json.write(itemRequestOutDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("нужна отвертка");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}