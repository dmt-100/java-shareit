package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws Exception {
        //given
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("отлично");
        commentDto.setItemId(1L);
        commentDto.setUserId(1L);
        commentDto.setAuthorName("Akhra");
        //when
        JsonContent<CommentDto> result = json.write(commentDto);
        //then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("отлично");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Akhra");
    }
}