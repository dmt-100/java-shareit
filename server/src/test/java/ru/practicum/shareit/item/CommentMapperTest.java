package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static ru.practicum.shareit.item.CommentRepositoryTest.createComment;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentMapperTest {
    Comment comment;
    CommentRequestDto requestDto;
    CommentResponseDto responseDto;

    @Test
    void commentToDto() {
        comment = createComment(new User(), new Item());
        responseDto = CommentMapper.commentToDto(comment);
        assertNotNull(responseDto);
        assertThat(comment.getId(), equalTo(responseDto.getId()));
    }

    @Test
    void dtoToComment() {
        requestDto = CommentRequestDto.builder().text("text").build();
        comment = CommentMapper.dtoToComment(requestDto);
        assertNotNull(comment);
        assertThat(requestDto.getText(), equalTo(comment.getText()));
    }
}
