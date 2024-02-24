package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDescriptionDtoTest {

    @Autowired
    private JacksonTester<ItemDescriptionDto> json;

    @Test
    void testItemDescriptionDto() throws Exception {
        //given
        ItemDescriptionDto itemDescriptionDto = new ItemDescriptionDto();
        itemDescriptionDto.setId(1L);
        itemDescriptionDto.setName("отвертка");
        itemDescriptionDto.setOwnerId(1L);
        //when
        JsonContent<ItemDescriptionDto> result = json.write(itemDescriptionDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("отвертка");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
    }
}