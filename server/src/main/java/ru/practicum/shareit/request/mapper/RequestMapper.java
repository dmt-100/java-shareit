package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemShortResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.model.RequestItem;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class RequestMapper {
    public RequestItem dtoToRequestItem(RequestItemDto dto) {
        return RequestItem.builder()
                .description(dto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public RequestItemResponseDto requestItemToResponseDto(RequestItem requestItem) {
        List<ItemShortResponseDto> items = ItemMapper.itemsToShortDtoList(requestItem.getItems());
        return RequestItemResponseDto.builder()
                .id(requestItem.getId())
                .description(requestItem.getDescription())
                .created(requestItem.getCreated())
                .items(items)
                .build();
    }
}
