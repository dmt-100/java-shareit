package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestItemResponseDto {
    long id;
    String description;
    LocalDateTime created;
    List<ItemShortResponseDto> items;
}
