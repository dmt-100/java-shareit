package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getAllItemRequestsByUser(Long userId);

    List<ItemRequestDto> getAllItemRequestsByOtherUsers(Long userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);

    ItemRequestDto saveItemRequest(ItemRequestDto itemRequestDto, Long userId);

}