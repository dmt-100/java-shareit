package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemRequestOutWithItemsDto;

import java.util.List;

public interface ItemRequestsService {
    ItemRequestOutDto addRequest(ItemRequestInDto itemRequestInDto);

    List<ItemRequestOutWithItemsDto> getAllUserRequests(Long userId);

    List<ItemRequestOutWithItemsDto> getAllRequests(Long userId, int from, int size);

    ItemRequestOutWithItemsDto getRequestById(Long userId, Long requestId);
}
