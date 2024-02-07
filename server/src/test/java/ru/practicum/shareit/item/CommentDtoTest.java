package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static ru.practicum.shareit.utils.Constants.FORMATTER_FOR_DATETIME;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @DisplayName("получен ДТО комментария, когда вызвана сериализация, " +
            "то получен сериализованный комментарий")
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("text 1");
        commentDto.setAuthorName("Name 1");
        commentDto.setCreated(LocalDateTime.now());

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().format(FORMATTER_FOR_DATETIME));
    }

}