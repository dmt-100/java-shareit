package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;

import java.util.List;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoOwnerTest {

    @Autowired
    private JacksonTester<ItemDtoOwner> json;

    @Test
    @DisplayName("получен ДТО вещи, когда вызвана сериализация, " +
            "то получен сериализованная вещь")
    void testItemDtoOwner() throws Exception {
        BookingDtoOwner lastBooking = new BookingDtoOwner();
        lastBooking.setId(3L);
        lastBooking.setBookerId(2L);
        BookingDtoOwner nextBooking = new BookingDtoOwner();
        nextBooking.setId(5L);
        nextBooking.setBookerId(2L);
        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(6L);
        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(8L);

        ItemDtoOwner itemDto = new ItemDtoOwner();
        itemDto.setId(1L);
        itemDto.setName("item 1");
        itemDto.setDescription("description 1");
        itemDto.setAvailable(true);
        itemDto.setRequestId(2L);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(List.of(commentDto1, commentDto2));

        JsonContent<ItemDtoOwner> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable().booleanValue());
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemDto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemDto.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemDto.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemDto.getNextBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemDto.getComments().get(0).getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id")
                .isEqualTo(itemDto.getComments().get(1).getId().intValue());
    }

}