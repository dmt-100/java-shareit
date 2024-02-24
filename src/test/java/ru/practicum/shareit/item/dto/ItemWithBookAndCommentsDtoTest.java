package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithBookAndCommentsDtoTest {
    @Autowired
    private JacksonTester<ItemWithBookAndCommentsDto> json;

    @Test
    void testItemDescriptionDto() throws Exception {
        //given
        ItemWithBookAndCommentsDto itemWithBookAndCommentsDto = new ItemWithBookAndCommentsDto();
        itemWithBookAndCommentsDto.setId(1L);
        itemWithBookAndCommentsDto.setName("отвертка");
        itemWithBookAndCommentsDto.setDescription("крестовая");
        itemWithBookAndCommentsDto.setAvailable(true);
        itemWithBookAndCommentsDto.setOwner(1L);
        itemWithBookAndCommentsDto.setRequestId(1L);
        itemWithBookAndCommentsDto.setLastBooking(null);
        itemWithBookAndCommentsDto.setNextBooking(null);
        itemWithBookAndCommentsDto.setComments(null);
        //when
        JsonContent<ItemWithBookAndCommentsDto> result = json.write(itemWithBookAndCommentsDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("отвертка");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("крестовая");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(null);
    }
}