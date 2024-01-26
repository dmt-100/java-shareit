package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ContentNotFountException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemRequestOutWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestsServiceImpl implements ItemRequestsService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestOutDto addRequest(ItemRequestInDto itemRequestInDto) {
        User requester = checkAndReturnUser(itemRequestInDto.getUserId());
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestInDto, requester);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.mapToItemRequestOutDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestOutWithItemsDto> getAllUserRequests(Long userId) {
        checkAndReturnUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterId(userId, Sort.by("created").descending());
        List<Item> items = itemRepository.findAllByRequestIdIn(requests.stream().map(ItemRequest::getId).collect(Collectors.toList()));
        return getItemRequests(requests, items);
    }

    @Override
    public List<ItemRequestOutWithItemsDto> getAllRequests(Long userId, int from, int size) {
        checkAndReturnUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId,
                PageRequest.of(from, size, Sort.by("created").descending()));
        List<Item> items = itemRepository.findAllByRequestIdIn(requests.stream().map(ItemRequest::getId).collect(Collectors.toList()));
        return getItemRequests(requests, items);
    }

    @Override
    public ItemRequestOutWithItemsDto getRequestById(Long userId, Long requestId) {
        checkAndReturnUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ContentNotFountException("Запрос не найден"));
        List<ItemDto> items = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.mapToItemRequestOutWithItemsDto(itemRequest, items);
    }

    private User checkAndReturnUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ContentNotFountException("Пользователь не найден"));
    }

    private List<ItemRequestOutWithItemsDto> getItemRequests(List<ItemRequest> requests, List<Item> items) {
        List<ItemRequestOutWithItemsDto> itemRequestOutWithItemsDto = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> requestItems = items.stream()
                    .filter(x -> x.getRequestId().equals(request.getId()))
                    .collect(Collectors.toList());
            items.removeAll(requestItems);
            List<ItemDto> requestItemDescriptionDto = requestItems.stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
            itemRequestOutWithItemsDto.add(ItemRequestMapper.mapToItemRequestOutWithItemsDto(request, requestItemDescriptionDto));
        }
        return itemRequestOutWithItemsDto;
    }
}
