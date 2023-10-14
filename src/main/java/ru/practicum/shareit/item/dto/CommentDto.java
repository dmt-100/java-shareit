package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.user.dto.UserDto;
import lombok.Data;

@Data
public class CommentDto {

    private Long id;
    private String text;
    private final ItemDto item;
    private final UserDto author;

}