package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDescriptionDto() throws Exception {
        //given
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("отвертка");
        itemDto.setDescription("крестовая");
        itemDto.setAvailable(true);
        itemDto.setOwner(1L);
        itemDto.setRequestId(1L);
        //when
        JsonContent<ItemDto> result = json.write(itemDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("отвертка");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("крестовая");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}