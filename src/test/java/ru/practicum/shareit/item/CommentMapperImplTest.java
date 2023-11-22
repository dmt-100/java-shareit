package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

class CommentMapperImplTest {

    private final CommentMapperImpl commentMapper = new CommentMapperImpl();

    @Test
    @DisplayName("получен маппер в ДТО комментария, когда вызван нуль, то получен нуль")
    void toCommentDto() {
        CommentDto commentDto = commentMapper.toCommentDto(null);

        assertThat(commentDto, nullValue());
    }

    @Test
    @DisplayName("получен маппер в ДТО комментария, когда вызван, то получен")
    void toCommentDtoWithAuthor() {
        Item item = new Item();
        item.setId(1L);
        User user = new User();
        user.setId(8L);

        Comment comment = new Comment();
        comment.setId(2L);
        comment.setText("text 2");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertThat(comment.getId(), equalTo(commentDto.getId()));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getAuthor().getName(), equalTo(commentDto.getAuthorName()));
        assertThat(comment.getCreated(), equalTo(commentDto.getCreated()));
    }

    @Test
    @DisplayName("получен маппер в комментарий, когда вызван нуль, то получен нуль")
    void toComment() {
        Comment comment = commentMapper.toComment(null, null, null);

        assertThat(comment, nullValue());
    }

    @Test
    @DisplayName("получен маппер в список ДТО комментариев, когда вызван нуль, то получен нуль")
    void convertCommentListToCommentDTOList() {
        List<CommentDto> comments = commentMapper.convertCommentListToCommentDTOList(null);

        assertThat(comments, nullValue());
    }

    @Test
    @DisplayName("получен маппер в список ДТО комментариев, когда вызван, то получен список")
    void convertCommentListToCommentDTOListNotEmpty() {
        Comment comment = new Comment();
        comment.setId(2L);
        comment.setText("text 2");

        List<CommentDto> comments = commentMapper.convertCommentListToCommentDTOList(Arrays.asList(comment));

        assertThat(1, equalTo(comments.size()));
    }

}