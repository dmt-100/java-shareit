package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    long id;
    String name;
    String email;
}
