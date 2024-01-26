package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingIdOutDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.BookingBadRequestException;
import ru.practicum.shareit.exceptions.ContentNotFountException;
import ru.practicum.shareit.exceptions.EditingNotAllowedException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto saveItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ContentNotFountException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long userId) {
        if (itemDto.getId() == null) {
            throw new ContentNotFountException("Необходимо указать id вещи");
        }
        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new ContentNotFountException("Вещь не найдена"));
        if (!userId.equals(item.getOwner().getId())) {
            throw new EditingNotAllowedException("Вещь может редактировать только ее владелец");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());

        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        //Валидация Item
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Item>> results = validator.validate(item);
        if (!results.isEmpty()) {
            throw new ConstraintViolationException(results);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookAndCommentsDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ContentNotFountException("Вещи с id = " + itemId + " не существует"));
        LocalDateTime targetDate = LocalDateTime.now();
        BookingIdOutDto lastBooking = getLastBooking(itemId, userId, targetDate);
        BookingIdOutDto nextBooking = getNextBooking(itemId, userId, targetDate);
        List<CommentDto> commentsDto = getItemComments(itemId);

        return ItemMapper.toItemWithBookAndCommentsDto(item,
                lastBooking, nextBooking,
                commentsDto == null ? new ArrayList<>() : commentsDto);
    }

    @Override
    public List<ItemWithBookAndCommentsDto> getItemsOfUser(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ContentNotFountException("Пользователь не найден"));
        LocalDateTime targetDate = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwnerId(userId,
                PageRequest.of(from, size, Sort.by("id").ascending()));
        HashMap<Long, BookingIdOutDto> itemLastBookings = getLastBookings(items.stream()
                .map(Item::getId).collect(Collectors.toList()), userId, targetDate);
        HashMap<Long, BookingIdOutDto> itemNextBookings = getNextBookings(items.stream()
                .map(Item::getId).collect(Collectors.toList()), userId, targetDate);
        HashMap<Long, List<CommentDto>> itemCommentsDto = getItemComments(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));

        List<ItemWithBookAndCommentsDto> itemsWithBookAndCommentsDto = new ArrayList<>();
        for (Item item : items) {
            itemsWithBookAndCommentsDto.add(ItemMapper.toItemWithBookAndCommentsDto(item,
                    itemLastBookings.get(item.getId()),
                    itemNextBookings.get(item.getId()),
                    itemCommentsDto.get(item.getId()) == null ? new ArrayList<>() : itemCommentsDto.get(item.getId())));
        }
        return itemsWithBookAndCommentsDto;
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameOrDescription(text,
                        PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto saveComment(CommentDto commentDto) {
        Item item = itemRepository.findById(commentDto.getItemId())
                .orElseThrow(() -> new ContentNotFountException("Вещь не найдена"));
        User user = userRepository.findById(commentDto.getUserId())
                .orElseThrow(() -> new ContentNotFountException("Пользователь не найден"));
        List<Booking> bookingsOfUser = bookingRepository.findByBookerIdAndEndIsBefore(commentDto.getUserId(),
                commentDto.getCreated(),
                PageRequest.of(0, 1000, Sort.by("start").descending()));
        List<Booking> itemBookingsOfUser = bookingsOfUser.stream()
                .filter(x -> Objects.equals(x.getItem().getId(), commentDto.getItemId()))
                .collect(Collectors.toList());
        if (itemBookingsOfUser.isEmpty()) {
            throw new BookingBadRequestException("Похоже пользователь не бронировал данную вещь");
        }
        Comment comment = CommentMapper.mapToComment(commentDto, item, user);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(savedComment);
    }

    private HashMap<Long, BookingIdOutDto> getLastBookings(List<Long> itemId, Long userId, LocalDateTime targetDate) {
        List<Booking> lastItemBookings = bookingRepository.findByItemIdIn(itemId, Sort.by("end").descending());
        HashMap<Long, BookingIdOutDto> itemBookings = new HashMap<>();
        for (Booking booking : lastItemBookings) {
            if (!itemBookings.containsKey(booking.getItem().getId())) {
                if (booking.getStart().isBefore(targetDate)
                        && booking.getItem().getOwner().getId().equals(userId)
                        && booking.getStatus().equals(Status.APPROVED)) {
                    itemBookings.put(booking.getItem().getId(), BookingMapper.mapToBookingIdOutDto(booking));
                }
            }
        }
        return itemBookings;
    }

    private BookingIdOutDto getLastBooking(Long itemId, Long userId, LocalDateTime targetDate) {
        List<Booking> lastItemBookings = bookingRepository.findByItemId(itemId, Sort.by("end").descending());
        for (Booking booking : lastItemBookings) {
            if (booking.getStart().isBefore(targetDate)
                    && booking.getItem().getOwner().getId().equals(userId)
                    && booking.getStatus().equals(Status.APPROVED)) return BookingMapper.mapToBookingIdOutDto(booking);
        }
        return null;
    }

    private HashMap<Long, BookingIdOutDto> getNextBookings(List<Long> itemId, Long userId, LocalDateTime targetDate) {
        List<Booking> nextItemBookings = bookingRepository.findByItemIdIn(itemId, Sort.by("start").ascending());
        HashMap<Long, BookingIdOutDto> itemBookings = new HashMap<>();
        for (Booking booking : nextItemBookings) {
            if (!itemBookings.containsKey(booking.getItem().getId())) {
                if (booking.getStart().isAfter(targetDate)
                        && booking.getItem().getOwner().getId().equals(userId)
                        && booking.getStatus().equals(Status.APPROVED)) {
                    itemBookings.put(booking.getItem().getId(), BookingMapper.mapToBookingIdOutDto(booking));
                }
            }
        }
        return itemBookings;
    }


    private BookingIdOutDto getNextBooking(Long itemId, Long userId, LocalDateTime targetDate) {
        List<Booking> nextItemBookings = bookingRepository.findByItemId(itemId, Sort.by("start").ascending());
        for (Booking booking : nextItemBookings) {
            if (booking.getStart().isAfter(targetDate)
                    && booking.getItem().getOwner().getId().equals(userId)
                    && booking.getStatus().equals(Status.APPROVED)) return BookingMapper.mapToBookingIdOutDto(booking);
        }
        return null;
    }

    private HashMap<Long, List<CommentDto>> getItemComments(List<Long> itemIds) {
        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);
        List<CommentDto> commentsDto = comments.stream().map(CommentMapper::mapToCommentDto).collect(Collectors.toList());
        HashMap<Long, List<CommentDto>> itemComments = new HashMap<>();
        for (CommentDto commentDto : commentsDto) {
            Long id = commentDto.getItemId();
            if (!itemComments.containsKey(id)) {
                itemComments.put(id, new ArrayList<>());
            }
            itemComments.get(id).add(commentDto);
        }
        return itemComments;
    }

    private List<CommentDto> getItemComments(Long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        return comments.stream().map(CommentMapper::mapToCommentDto).collect(Collectors.toList());
    }
}
