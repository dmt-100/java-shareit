package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;

@Getter
public class ItemDto {

    private Long id;

    @NotEmpty(message = "Ошибка! Краткое название вещи не может быть пустым.")
    private String name;

    @NotEmpty(message = "Ошибка! Развёрнутое описание вещи не может быть пустым.")
    private String description;

    private Boolean available;

    private Long requestId;

}