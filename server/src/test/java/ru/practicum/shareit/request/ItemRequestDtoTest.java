package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.assertj.core.util.Sets;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @DisplayName("получен ДТО запроса, когда вызвана сериализация, " +
            "то получен сериализованный запрос")
    void testItemRequestDto() throws Exception {
        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(3L);
        ItemDto itemDto2 = new ItemDto();
        itemDto2.setId(4L);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description 1");
        itemRequestDto.setCreated(Instant.now());
        itemRequestDto.setItems(Sets.set(itemDto1, itemDto2));
        List<ItemDto> items = new ArrayList<>(itemRequestDto.getItems());

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().toString());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(items.get(0).getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id")
                .isEqualTo(items.get(1).getId().intValue());
    }

}