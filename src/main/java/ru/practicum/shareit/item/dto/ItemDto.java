package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;
    @NotEmpty(message = "Ошибка! Краткое название вещи не может быть пустым.")
    private String name;
    @NotEmpty(message = "Ошибка! Развёрнутое описание вещи не может быть пустым.")
    private String description;
    private Boolean available;
    private Long request;

}