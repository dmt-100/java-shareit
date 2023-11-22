package ru.practicum.shareit.item.dto;

import static ru.practicum.shareit.utils.Constants.PATTERN_FOR_DATETIME;

import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class CommentDto {

    private Long id;

    @NotEmpty(message = "Ошибка! Текст комментария не может быть пустым.")
    private String text;

    private String authorName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime created;

}