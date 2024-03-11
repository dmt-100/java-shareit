package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestItemResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.service.RequestItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestItemController {
    RequestItemService requestItemService;

    @PostMapping
    public RequestItemResponseDto addRequest(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                             @RequestBody RequestItemDto request) {
        RequestItem requestItem = requestItemService.addRequest(ownerId, RequestMapper.dtoToRequestItem(request));
        return RequestMapper.requestItemToResponseDto(requestItem);
    }

    @GetMapping
    public List<RequestItemResponseDto> getOwnerRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return requestItemService.getOwnerRequests(ownerId)
                .stream()
                .map(RequestMapper::requestItemToResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<RequestItemResponseDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return requestItemService.getAllRequests(ownerId, from, size)
                .stream()
                .map(RequestMapper::requestItemToResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public RequestItemResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @PathVariable long requestId) {
        return RequestMapper.requestItemToResponseDto(
                requestItemService.getRequestById(ownerId, requestId));
    }
}
