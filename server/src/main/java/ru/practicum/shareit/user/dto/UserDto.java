package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Value
@Data
@Builder
public class UserDto {
    long id;
    String name;
    String email;
}
