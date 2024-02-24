package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static Comment mapToComment(CommentDto commentDto, Item item, User user) {
        if (commentDto == null) return null;
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        if (comment == null) return null;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItemId(comment.getItem().getId());
        commentDto.setUserId(comment.getUser().getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getUser().getName());
        return commentDto;
    }
}

