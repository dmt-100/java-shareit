package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(ItemRequestInDto itemRequestInDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(itemRequestInDto.getCreated());
        itemRequest.setId(itemRequestInDto.getId());
        itemRequest.setDescription(itemRequestInDto.getDescription());
        itemRequest.setRequester(requester);
        return itemRequest;
    }

    public static ItemRequestOutWithItemsDto mapToItemRequestOutWithItemsDto(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestOutWithItemsDto itemRequestOutWithItemsDto = new ItemRequestOutWithItemsDto();
        itemRequestOutWithItemsDto.setId(itemRequest.getId());
        itemRequestOutWithItemsDto.setCreated(itemRequest.getCreated());
        itemRequestOutWithItemsDto.setItems(items);
        itemRequestOutWithItemsDto.setDescription(itemRequest.getDescription());
        return itemRequestOutWithItemsDto;
    }

    public static ItemRequestOutDto mapToItemRequestOutDto(ItemRequest itemRequest) {
        ItemRequestOutDto itemRequestOutDto = new ItemRequestOutDto();
        itemRequestOutDto.setId(itemRequest.getId());
        itemRequestOutDto.setCreated(itemRequest.getCreated());
        itemRequestOutDto.setUserId(itemRequest.getRequester().getId());
        itemRequestOutDto.setDescription(itemRequest.getDescription());
        return itemRequestOutDto;
    }

    public static ItemRequestInDto mapToItemRequestInDto(ItemRequest itemRequest) {
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto();
        itemRequestInDto.setId(itemRequest.getId());
        itemRequestInDto.setCreated(itemRequest.getCreated());
        itemRequestInDto.setUserId(itemRequest.getRequester().getId());
        itemRequestInDto.setDescription(itemRequest.getDescription());
        return itemRequestInDto;
    }

}
