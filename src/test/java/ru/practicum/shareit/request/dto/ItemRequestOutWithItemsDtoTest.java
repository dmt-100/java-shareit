package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestOutWithItemsDtoTest {

    @Autowired
    private JacksonTester<ItemRequestOutWithItemsDto> json;

    @Test
    void testItemRequestOutWithItemsDto() throws Exception {
        //given
        LocalDateTime createdTime = LocalDateTime.now();
        ItemRequestOutWithItemsDto itemRequestOutWithItemsDto = new ItemRequestOutWithItemsDto();
        itemRequestOutWithItemsDto.setDescription("нужна отвертка");
        itemRequestOutWithItemsDto.setCreated(createdTime);
        itemRequestOutWithItemsDto.setId(1L);
        itemRequestOutWithItemsDto.setItems(null);
        //when
        JsonContent<ItemRequestOutWithItemsDto> result = json.write(itemRequestOutWithItemsDto);
        //then
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("нужна отвертка");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.items").isEqualTo(null);
    }
}