package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.BookingBadRequestException;
import ru.practicum.shareit.exceptions.ContentNotFountException;
import ru.practicum.shareit.exceptions.EditingNotAllowedException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private ItemDto createItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(null);
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setAvailable(true);
        itemDto.setDescription("мощная");
        itemDto.setOwner(1L);
        return itemDto;
    }

    private User createUser() {
        User user = new User();
        user.setEmail("akhraa1@yandex.ru");
        user.setId(1L);
        user.setName("Akhra");
        return user;
    }

    @Test
    void saveItem_whenOwnerFound_thenReturnSavedItem() {
        //given
        ItemDto itemDto = createItem();
        User owner = createUser();
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto, owner));

        assertThat(itemService.saveItem(itemDto, 1L), equalTo(itemDto));
    }

    @Test
    void saveItem_whenOwnerNotFound_thenContentNotFountExceptionThrown() {
        //given
        ItemDto itemDto = createItem();
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemService.saveItem(itemDto, 1L));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItem_whenItemIdIsNull_thenContentNotFountExceptionThrown() {
        //given
        ItemDto itemDto = createItem();
        itemDto.setId(null);
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemService.patchItem(itemDto, 1L));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItem_whenItemNotFound_thenContentNotFountExceptionThrown() {
        //given
        ItemDto itemDto = createItem();
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemService.patchItem(itemDto, 1L));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItem_whenUserNotOwner_thenEditingNotAllowedExceptionThrown() {
        //given
        ItemDto itemDto = createItem();
        User user = createUser();
        Item item = ItemMapper.toItem(itemDto, user);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        Assertions.assertThrows(
                EditingNotAllowedException.class,
                () -> itemService.patchItem(itemDto, 2L));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItem_whenItemNotValid_thenConstraintViolationExceptionThrown() {
        //given
        User user = createUser();
        ItemDto oldItemDto = createItem();
        oldItemDto.setAvailable(null);
        Item oldItem = ItemMapper.toItem(oldItemDto, user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));

        Assertions.assertThrows(
                ConstraintViolationException.class,
                () -> itemService.patchItem(oldItemDto, 1L));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItem_whenEditingAllowed_thenReturnUpdatedItem() {
        //given
        User user = createUser();
        ItemDto oldItemDto = createItem();
        Item oldItem = ItemMapper.toItem(oldItemDto, user);
        ItemDto newItemDto = createItem();
        newItemDto.setName("Перфаратор");
        Item newItem = ItemMapper.toItem(newItemDto, user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(any())).thenReturn(oldItem);

        itemService.patchItem(newItemDto, 1L);

        InOrder inOrder = inOrder(itemRepository);
        inOrder.verify(itemRepository, times(1)).findById(any());
        inOrder.verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertThat(savedItem.getName(), equalTo(newItem.getName()));
        assertThat(savedItem.getDescription(), equalTo(oldItemDto.getDescription()));
    }

    @Test
    void getItemById_whenInvoked_thenReturnItemWithFilteredBookingsAndComments() {
        //given
        ItemDto itemDto = createItem();
        User user = createUser();
        Item item = ItemMapper.toItem(itemDto, user);

        List<Booking> itemBookings = getBookings(item);
        List<Comment> itemComments = getComments(item);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemId(anyLong(), any())).thenReturn(itemBookings);
        when(commentRepository.findByItemId(anyLong())).thenReturn(itemComments);

        ItemWithBookAndCommentsDto items = itemService.getItemById(1L, 1L);

        assertThat(items.getComments().size(), equalTo(2));
        assertThat(items.getLastBooking().getId(), equalTo(1L));
        assertThat(items.getNextBooking().getId(), equalTo(3L));
    }

    @Test
    void getItemsOfUser_whenInvoked_thenReturnItemsListWithFilteredBookingsAndComments() {
        //given
        ItemDto itemDto = createItem();
        User user = createUser();
        Item item1 = ItemMapper.toItem(itemDto, user);

        ItemDto itemDto2 = createItem();
        itemDto2.setId(2L);
        Item item2 = ItemMapper.toItem(itemDto2, user);

        List<Booking> item1Bookings = getBookings(item1);
        List<Booking> item2Bookings = getBookings(item2);
        List<Booking> itemsBookings = Stream.of(item1Bookings, item2Bookings)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Comment> item1Comments = getComments(item1);
        List<Comment> item2Comments = getComments(item2);
        List<Comment> itemsComments = Stream.of(item1Comments, item2Comments)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item1, item2));
        when(bookingRepository.findByItemIdIn(anyList(), any())).thenReturn(itemsBookings);
        when(commentRepository.findByItemIdIn(anyList())).thenReturn(itemsComments);

        List<ItemWithBookAndCommentsDto> items = itemService.getItemsOfUser(1L, 0, 10);

        assertThat(items.size(), equalTo(2));

        assertThat(items.get(0).getId(), equalTo(1L));
        assertThat(items.get(0).getComments().size(), equalTo(2));
        assertThat(items.get(1).getId(), equalTo(2L));
        assertThat(items.get(1).getComments().size(), equalTo(2));

        assertThat(items.get(0).getLastBooking().getId(), equalTo(1L));
        assertThat(items.get(1).getLastBooking().getId(), equalTo(1L));

        assertThat(items.get(0).getNextBooking().getId(), equalTo(3L));
        assertThat(items.get(1).getNextBooking().getId(), equalTo(3L));
    }

    @Test
    void searchItems_whenInvoked_thenReturnListOfItems() {
        //when
        itemService.searchItems("hi", 0, 1);
        //then
        verify(itemRepository, times(1)).findAllByNameOrDescription(anyString(), any());
    }

    @Test
    void saveComment_whenItemNotFound_thenContentNotFountExceptionThrown() {
        //given
        ItemDto itemDto = createItem();
        User user = createUser();
        Item item = ItemMapper.toItem(itemDto, user);
        CommentDto commentDto = CommentMapper.mapToCommentDto(getComments(item).get(0));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemService.saveComment(commentDto));
    }

    @Test
    void saveComment_whenUserNotFound_thenContentNotFountExceptionThrown() {
        //given
        ItemDto itemDto = createItem();
        User user = createUser();
        Item item = ItemMapper.toItem(itemDto, user);
        CommentDto commentDto = CommentMapper.mapToCommentDto(getComments(item).get(0));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemService.saveComment(commentDto));
    }

    @Test
    void saveComment_whenUserNotBookedItem_thenBookingBadRequestExceptionThrown() {
        //given
        ItemDto itemDto = createItem();
        User user = createUser();
        Item item = ItemMapper.toItem(itemDto, user);
        CommentDto commentDto = CommentMapper.mapToCommentDto(getComments(item).get(0));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Assertions.assertThrows(
                BookingBadRequestException.class,
                () -> itemService.saveComment(commentDto));
    }

    @Test
    void saveComment_whenUserTrulyBookedItem_thenReturnSavedComment() {
        //given
        ItemDto itemDto = createItem();
        User user = createUser();
        Item item = ItemMapper.toItem(itemDto, user);
        CommentDto commentDto = CommentMapper.mapToCommentDto(getComments(item).get(0));
        Booking booking = getBookings(item).get(0);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(List.of(booking));
        //when
        itemService.saveComment(commentDto);
        //then
        verify(commentRepository, times(1)).save(any());
    }

    private List<Booking> getBookings(Item item) {
        User user = createUser();
        user.setId(2L);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.now().minusDays(5));
        booking1.setEnd(LocalDateTime.now().minusDays(4));
        booking1.setItem(item);
        booking1.setStatus(Status.APPROVED);
        booking1.setBooker(user);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(LocalDateTime.now().minusDays(3));
        booking2.setEnd(LocalDateTime.now().minusDays(2));
        booking2.setItem(item);
        booking2.setStatus(Status.REJECTED);
        booking2.setBooker(user);

        Booking booking3 = new Booking();
        booking3.setId(3L);
        booking3.setStart(LocalDateTime.now().plusDays(1));
        booking3.setEnd(LocalDateTime.now().plusDays(2));
        booking3.setItem(item);
        booking3.setStatus(Status.APPROVED);
        booking3.setBooker(user);

        Booking booking4 = new Booking();
        booking4.setId(4L);
        booking4.setStart(LocalDateTime.now().plusDays(3));
        booking4.setEnd(LocalDateTime.now().plusDays(4));
        booking4.setItem(item);
        booking4.setStatus(Status.APPROVED);
        booking4.setBooker(user);

        return List.of(booking1, booking2, booking3, booking4);
    }

    private List<Comment> getComments(Item item) {
        User user = createUser();
        user.setId(2L);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setItem(item);
        comment1.setText("мощный агрегат");
        comment1.setUser(user);
        comment1.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment1.setId(2L);
        comment2.setItem(item);
        comment2.setText("тяжеловат");
        comment2.setUser(user);
        comment2.setCreated(LocalDateTime.now());


        return List.of(comment1, comment2);

    }
}