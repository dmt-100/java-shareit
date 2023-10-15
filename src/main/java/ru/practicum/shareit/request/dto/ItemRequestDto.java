package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    @NotNull(message = "Пользователь не может быть null.")
    private UserDto requestor;

}