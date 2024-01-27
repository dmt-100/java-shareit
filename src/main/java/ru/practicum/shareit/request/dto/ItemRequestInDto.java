package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestInDto {
    private Long id;

    @NotNull
    private String description;

    private Long userId;

    private LocalDateTime created;
}
