package ru.practicum.shareit.item.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemShortResponseDto {
    long id;
    String name;
    String description;
    boolean available;
    long requestId;
}
