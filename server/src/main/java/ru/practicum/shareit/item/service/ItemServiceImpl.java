package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.utils.ValidPage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId, Integer from, Integer size) {
        ValidPage.validate(from, size);
        PageRequest page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));

        return itemRepository.findAllByOwnerId(userId, page)
                .stream()
                .map(item -> getItemById(item.getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь с идентификатором " + itemId + " не найдена."));

        Booking lastBooking = setLastBooking(item, userId);
        Booking nextBooking = setNextBooking(item, userId);

        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return ItemMapper.INSTANCE.toItemDtoOwner(item, lastBooking, nextBooking, comments);
    }

    @Transactional
    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        itemDto = validateItemDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + userId + " не найден."));

        Item item;
        if (itemDto.getRequestId() != null) {
            Optional<ItemRequest> itemRequest = itemRequestRepository.findById(itemDto.getRequestId());
            if (itemRequest.isPresent()) {
                item = ItemMapper.INSTANCE.toItemWithRequest(itemDto, user, itemRequest.get());
            } else {
                item = ItemMapper.INSTANCE.toItem(itemDto, user);
            }
        } else {
            item = ItemMapper.INSTANCE.toItem(itemDto, user);
        }

        try {
            return ItemMapper.INSTANCE.toItemDto(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new ItemNotSaveException("Вещь не была создана: " + itemDto);
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь с id = " + itemId + " не найдена."));
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
    public List<ItemDto> findItems(String text, Long userId, Integer from, Integer size) {
        if (text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        PageRequest page = ValidPage.validate(from, size);

        return ItemMapper.INSTANCE.convertItemListToItemDTOList(
                itemRepository.search(text, page));
    }

    @Transactional
    @Override
    public CommentDto saveComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь с идентификатором " + itemId + " не найдена."));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с id = " + userId + " не найден."));
        if (bookingRepository.isFindBooking(itemId, userId, LocalDateTime.now()) == null) {
            throw new ValidationException("Ошибка!  Отзыв может оставить только тот пользователь, " +
                    "который брал эту вещь в аренду, и только после окончания срока аренды.", 20002);
        }

        Comment comment = CommentMapper.INSTANCE.toComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());

        try {
            return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new CommentNotSaveException("Комментарий не был создан: " + commentDto);
        }
    }

    private ItemDto validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка! Статус доступности вещи для аренды " +
                    "не может быть пустым.", 20001);
        }
        return itemDto;
    }

    private Booking setLastBooking(Item item, Long userId) {
        Booking lastBooking;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsBefore(
                    item.getId(), StatusBooking.APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "end")).orElse(null);
        } else {
            lastBooking = null;
        }
        return lastBooking;
    }

    private Booking setNextBooking(Item item, Long userId) {
        Booking nextBooking;
        if (item.getOwner().getId().equals(userId)) {
            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsAfter(
                    item.getId(), StatusBooking.APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.ASC, "start")).orElse(null);
        } else {
            nextBooking = null;
        }
        return nextBooking;
    }

}