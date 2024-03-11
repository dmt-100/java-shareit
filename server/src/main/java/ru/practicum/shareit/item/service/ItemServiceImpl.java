package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;

    @Override
    public List<Item> getOwnerItems(long ownerId) {
        List<Item> itemList = itemRepository.findAllByOwnerIdOrderById(ownerId);
        List<Long> itemIds = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndItemIds(ownerId, itemIds);
        List<Comment> comments = commentRepository.findAllByItemIds(itemIds);
        return itemList.stream()
                .map(item -> setBookingsAndComments(item, bookingList, comments))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(long id, long ownerId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such item"));
        List<Booking> bookingList = bookingRepository.findAllByOwnerIdAndItemId(ownerId, id);
        List<Comment> comments = commentRepository.findAllByItemId(id);
        return setBookingsAndComments(item, bookingList, comments);
    }

    @Transactional
    @Override
    public Item addItem(Item item, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("No such owner"));
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(Item item, long ownerId, long id) {
        Item currentItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No such item"));
        if (currentItem.getOwner().getId() != ownerId) {
            throw new NotFoundException("Wrong owner id");
        }
        if (item.getName() != null
                && (!item.getName().isBlank())) currentItem.setName(item.getName());
        if (item.getDescription() != null
                && (!item.getDescription().isBlank())) currentItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) currentItem.setAvailable(item.getAvailable());
        return currentItem;
    }

    @Override
    public List<Item> getItemByNameOrDescription(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, true);
    }

    @Transactional
    @Override
    public Comment addComment(long ownerId, long itemId, Comment comment) {
        if (bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(ownerId, itemId, LocalDateTime.now()).isEmpty())
            throw new BadRequestException("user doesn't have bookings for this item");

        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("no such user"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("no such item"));
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private Item setBookingsAndComments(Item item, List<Booking> bookingList, List<Comment> comments) {
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (!bookingList.isEmpty()) {
            LocalDateTime currentTime = LocalDateTime.now();
            Optional<Booking> lastOptBooking = bookingList.stream()
                    .filter(b -> b.getItem().getId() == item.getId() && b.getStatus().equals(Status.APPROVED))
                    .filter(b -> !b.getStart().isAfter(currentTime))
                    .findFirst();

            Optional<Booking> nextOptBooking = bookingList.stream()
                    .filter(b -> b.getItem().getId() == item.getId() && b.getStatus().equals(Status.APPROVED))
                    .filter(b -> b.getStart().isAfter(currentTime))
                    .reduce((first, second) -> second);
            if (lastOptBooking.isPresent())
                lastBooking = lastOptBooking.get();
            if (nextOptBooking.isPresent())
                nextBooking = nextOptBooking.get();
        }
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
