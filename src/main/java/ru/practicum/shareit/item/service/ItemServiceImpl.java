package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotUpdateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotSaveException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemOtherOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId, Sort.by(Sort.Direction.ASC, "id"));
        return items.stream()
                .map(item -> getItemById(item.getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь с идентификатором " + itemId + " не найдена."));

        Booking lastBooking;
        Booking nextBooking;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndEndIsBefore(itemId, LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "start")).orElse(null);
            nextBooking = bookingRepository.findFirstByItemIdAndEndIsAfter(itemId, LocalDateTime.now(),
                    Sort.by(Sort.Direction.ASC, "start")).orElse(null);
        } else {
            lastBooking = null;
            nextBooking = null;
        }

        return ItemMapper.INSTANCE.toItemDtoOwner(item, lastBooking, nextBooking);
    }

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        ItemDto itemDtoNew = validateItemDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + userId + " не найден."));
        Item item = ItemMapper.INSTANCE.toItem(itemDto, user);

        try {
            return ItemMapper.INSTANCE.toItemDto(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new ItemNotSaveException("Вещь не была создана: " + itemDtoNew);
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь с id = " + itemId + "не найдена."));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemOtherOwnerException(String.format("Пользователь с id = " + userId +
                    " не является владельцем вещи: " + itemDto));
        }
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + userId + " не найден."));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        try {
            return ItemMapper.INSTANCE.toItemDto(itemRepository.saveAndFlush(item));
        } catch (DataIntegrityViolationException e) {
            throw new ItemNotUpdateException("Вещь с id = " + itemId + " не была обновлена: " + itemDto);
        }
    }

    @Override
    public List<ItemDto> findItems(String text, Long userId) {
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return ItemMapper.INSTANCE.convertItemListToItemDTOList(itemRepository.search(text));
    }

    private ItemDto validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка! Статус доступности вещи для аренды не может быть пустым.", 20001);
        }
        return itemDto;
    }

}