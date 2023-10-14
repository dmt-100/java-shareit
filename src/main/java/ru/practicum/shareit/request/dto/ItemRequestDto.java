package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserDto;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    private UserDto requestor;

}