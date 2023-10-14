package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class ItemDto {

    private Long id;
    @NotEmpty(message = "Ошибка! Краткое название вещи не может быть пустым.")
    private String name;
    @NotEmpty(message = "Ошибка! Развёрнутое описание вещи не может быть пустым.")
    private String description;
    private Boolean available;
    private Set<ItemRequestDto> request = new HashSet<>();

}