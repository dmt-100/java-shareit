package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.item.ItemControllerTest.createItem;
import static ru.practicum.shareit.item.ItemControllerTest.createOwner;

@ExtendWith(MockitoExtension.class)
public class ItemServiceMockTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    static User createBooker() {
        return User.builder()
                .id(2)
                .name("booker")
                .email("booker@booker.com")
                .build();
    }

    static Booking createBooking(User booker, Item item) {
        return Booking.builder()
                .id(1)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(3))
                .build();
    }

    static Comment createComment(User booker, Item item) {
        return Comment.builder()
                .id(1)
                .item(item)
                .created(LocalDateTime.now())
                .author(booker)
                .text("text")
                .build();
    }

    @Test
    void addItemWithValidData() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any()))
                .thenReturn(item);
        Item result = itemService.addItem(item, owner.getId());
        assertThat(result, equalTo(item));
        assertThat(item.getOwner(), equalTo(owner));
    }

    @Test
    void addItemWithoutOwnerEntity() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addItem(item, owner.getId()));
        assertThat(exception.getMessage(), equalTo("No such owner"));
    }

    @Test
    void updateItemWithValidData() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Item result = itemService.updateItem(item, owner.getId(), item.getId());
        assertThat(result, equalTo(item));
        assertThat(item.getOwner(), equalTo(owner));
    }

    @Test
    void updateItemWithoutOwnerEntity() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item, owner.getId(), item.getId()));
        assertThat(exception.getMessage(), equalTo("No such item"));
    }

    @Test
    void updateItemWithWrongOwnerId() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(item, owner.getId() + 1, item.getId()));
        assertThat(exception.getMessage(), equalTo("Wrong owner id"));
    }

    @Test
    void deleteItem() {
        Item item = createItem(createOwner());
        itemRepository.save(item);
        itemRepository.deleteById(item.getId());
        assertThat(itemRepository.existsById(item.getId()), equalTo(false));
        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    @Test
    void getOwnerItemsWithValidData() {
        User owner = createOwner();
        User booker = createBooker();
        Item item = createItem(owner);
        Booking booking = createBooking(booker, item);
        Comment comment = createComment(booker, item);
        when(itemRepository.findAllByOwnerIdOrderById(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.findAllByOwnerIdAndItemIds(anyLong(), anyList()))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIds(anyList()))
                .thenReturn(List.of(comment));
        List<Item> result = itemService.getOwnerItems(owner.getId());
        assertThat(result.get(0).getId(), equalTo(item.getId()));
    }

    @Test
    void getItemWithValidData() {
        User owner = createOwner();
        User booker = createBooker();
        Item item = createItem(owner);
        Booking booking = createBooking(booker, item);
        Comment comment = createComment(booker, item);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByOwnerIdAndItemId(owner.getId(), item.getId()))
                .thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(comment));
        Item result = itemService.getItem(item.getId(), owner.getId());
        assertThat(item.getId(), equalTo(result.getId()));
    }

    @Test
    void getItemWithoutItemEntity() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItem(item.getId(), owner.getId()));
        assertThat(exception.getMessage(), equalTo("No such item"));
    }

    @Test
    void getItemByNameOrDescription() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(
                anyString(), anyString(), anyBoolean()))
                .thenReturn(List.of(item));
        List<Item> result = itemService.getItemByNameOrDescription("pen");
        assertThat(result.get(0).getId(), equalTo(item.getId()));
    }

    @Test
    void getItemByNameOrDescriptionWithoutText() {
        User owner = createOwner();
        Item item = createItem(owner);
        itemRepository.save(item);
        List<Item> result = itemService.getItemByNameOrDescription("");
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void addCommentWithValidData() {
        User owner = createOwner();
        User booker = createBooker();
        Item item = createItem(owner);
        Booking booking = createBooking(booker, item);
        Comment comment = createComment(booker, item);
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        itemService.addComment(booker.getId(), item.getId(), comment);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addCommentWithoutUserBookings() {
        User owner = createOwner();
        User booker = createBooker();
        Item item = createItem(owner);
        Comment comment = createComment(booker, item);
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Collections.emptyList());
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.addComment(booker.getId(), item.getId(), comment));
        assertThat(exception.getMessage(), equalTo("user doesn't have bookings for this item"));
    }

    @Test
    void addCommentWithoutUserEntity() {
        User owner = createOwner();
        User booker = createBooker();
        Item item = createItem(owner);
        Booking booking = createBooking(booker, item);
        Comment comment = createComment(booker, item);
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(booker.getId(), item.getId(), comment));
        assertThat(exception.getMessage(), equalTo("no such user"));
    }

    @Test
    void addCommentWithoutItemEntity() {
        User owner = createOwner();
        User booker = createBooker();
        Item item = createItem(owner);
        Booking booking = createBooking(booker, item);
        Comment comment = createComment(booker, item);
        when(bookingRepository.findAllByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(List.of(booking));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(booker.getId(), item.getId(), comment));
        assertThat(exception.getMessage(), equalTo("no such item"));
    }
}
