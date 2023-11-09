package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    @DisplayName("получены все вещи, когда вызваны по умолчанию, то получен пустой список")
    void getAllItemsByUser_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;

        List<ItemDto> actualItems = itemService.getAllItemsByUser(userId, 0, 1);

        assertThat(actualItems, empty());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findAllByItemId(anyLong());
        verify(itemRepository, times(1))
                .findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все вещи, когда вызваны, то получен непустой список")
    void getAllItemsByUser_whenInvoked_thenReturnedItemsCollectionInList() {
        long userId = 0L;
        User user = new User();
        user.setId(1L);
        long itemId = 0L;
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(user);
        List<Item> expectedItems = Arrays.asList(item);

        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(expectedItems);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        List<ItemDto> actualItems = itemService.getAllItemsByUser(userId, 0, 1);

        assertThat(expectedItems.size(), equalTo(actualItems.size()));
        assertThat(expectedItems.get(0).getId(), equalTo(actualItems.get(0).getId()));
        assertThat(expectedItems.get(0).getName(), equalTo(actualItems.get(0).getName()));
        assertThat(expectedItems.get(0).getDescription(), equalTo(actualItems.get(0).getDescription()));
        assertThat(expectedItems.get(0).getAvailable(), equalTo(actualItems.get(0).getAvailable()));
        assertThat(expectedItems.get(0).getRequest(), equalTo(actualItems.get(0).getRequestId()));

        InOrder inOrder = inOrder(itemRepository, commentRepository);
        inOrder.verify(itemRepository, times(1))
                .findAllByOwnerId(anyLong(), any(Pageable.class));
        inOrder.verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }


    @Test
    @DisplayName("получена вещь по ид, когда вещь найдена, тогда она возвращается")
    void getItemById_whenItemFound_thenReturnedItem() {
        long itemId = 0L;
        long userId = 0L;
        Item expectedItem = new Item();
        User user = new User();
        user.setId(1L);
        expectedItem.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.EMPTY_LIST);

        ItemDto actualItem = itemService.getItemById(itemId, userId);

        assertThat(ItemMapper.INSTANCE.toItemDtoOwner(expectedItem,
                null, null, Collections.EMPTY_LIST), equalTo(actualItem));
        InOrder inOrder = inOrder(itemRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    @Test
    @DisplayName("получена вещь по ид, когда вещь не найдена, тогда выбрасывается исключение")
    void getItemById_whenItemNotFound_thenExceptionThrown() {
        long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(itemId, 0L));

        assertThat("Вещь с идентификатором 0 не найдена.", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(itemId);
    }

    @Test
    @DisplayName("сохранена вещь, когда вещь валидна, тогда она сохраняется")
    void saveItem_whenItemValid_thenSavedItem() {
        ItemDto itemToSave = new ItemDto();
        itemToSave.setAvailable(true);
        Long userId = 0L;
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(ItemMapper.INSTANCE.toItem(itemToSave, user));

        ItemDto actualItem = itemService.saveItem(itemToSave, userId);

        assertThat(itemToSave, equalTo(actualItem));
        InOrder inOrder = inOrder(userRepository, itemRepository);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("сохранена вещь, когда вещь с запросом, тогда она сохраняется")
    void saveItem_whenItemWithRequest_thenSavedItem() {
        Long userId = 0L;
        User user = new User();
        ItemDto itemToSave = new ItemDto();
        itemToSave.setAvailable(true);
        itemToSave.setRequestId(1L);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        Item item = ItemMapper.INSTANCE.toItem(itemToSave, user);
        item.setRequest(itemRequest);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto actualItem = itemService.saveItem(itemToSave, userId);

        assertThat(itemToSave, equalTo(actualItem));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository, itemRepository);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRequestRepository, times(1)).findById(1L);
        inOrder.verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("сохранена вещь, когда вещь не валидна, тогда выбрасывается исключение")
    void saveItem_whenItemNotValid_thenExceptionThrown() {
        ItemDto itemToSave = new ItemDto();
        itemToSave.setAvailable(true);
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.save(any(Item.class)))
                .thenThrow(new ItemNotSaveException("Вещь не была создана"));

        final ItemNotSaveException exception = assertThrows(ItemNotSaveException.class,
                () -> itemService.saveItem(itemToSave, userId));

        assertThat("Вещь не была создана", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("сохранена вещь, когда пользователь вещи не найден, тогда выбрасывается исключение")
    void saveItem_whenUserNotFound_thenExceptionThrown() {
        ItemDto itemToSave = new ItemDto();
        itemToSave.setAvailable(true);
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.saveItem(itemToSave, userId));

        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRepository, never()).save(any(Item.class));
    }


    @Test
    @DisplayName("обновлена вещь, когда вещь валидна, тогда она обновляется")
    void updateItem_whenItemFound_thenUpdatedItemOnlyAvailableFields() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Long itemId = 0L;
        Item oldItem = new Item();
        oldItem.setName("1");
        oldItem.setDescription("1");
        oldItem.setAvailable(false);
        oldItem.setOwner(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(5L);
        Item newItem = new Item();
        newItem.setName("2");
        newItem.setDescription("2");
        newItem.setAvailable(true);
        newItem.setRequest(itemRequest);

        itemService.updateItem(itemId, ItemMapper.INSTANCE.toItemDto(newItem), userId);
        verify(itemRepository).saveAndFlush(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertThat(newItem.getName(), equalTo(savedItem.getName()));
        assertThat(newItem.getDescription(), equalTo(savedItem.getDescription()));
        assertThat(newItem.getAvailable(), equalTo(savedItem.getAvailable()));

        InOrder inOrder = inOrder(userRepository, itemRepository);
        verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRepository, times(1)).saveAndFlush(any(Item.class));
    }

    @Test
    @DisplayName("обновлена вещь, когда вещь не найдена, тогда выбрасывается исключение")
    void updateItem_whenItemNotFound_thenExceptionThrown() {
        Long userId = 0L;
        Long itemId = 0L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemId, new ItemDto(), userId));

        assertThat("Вещь с id = 0 не найдена.", equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).findById(userId);
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    @DisplayName("обновлена вещь, когда пользователь не является владельцем вещи, " +
            "тогда выбрасывается исключение")
    void updateItem_whenUserNotValid_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(1L);
        Long itemId = 0L;
        Item oldItem = new Item();
        oldItem.setOwner(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));

        final ItemOtherOwnerException exception = assertThrows(ItemOtherOwnerException.class,
                () -> itemService.updateItem(itemId, new ItemDto(), userId));

        assertThat(String.format("Пользователь с id = 0 не является владельцем вещи: " + new ItemDto()),
                equalTo(exception.getMessage()));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, never()).findById(userId);
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    @DisplayName("обновлена вещь, когда пользователь не найден, тогда выбрасывается исключение")
    void updateItem_whenUserNotFound_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Long itemId = 0L;
        Item oldItem = new Item();
        oldItem.setOwner(user);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(itemId, new ItemDto(), userId));

        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(itemRepository, userRepository);
        inOrder.verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    @DisplayName("обновлена вещь, когда вещь не может быть обновлена, тогда выбрасывается исключение")
    void updateItem_whenItemNotUpdate_thenExceptionThrown() {
        Long userId = 0L;
        User user = new User();
        user.setId(userId);
        Long itemId = 0L;
        Item oldItem = new Item();
        oldItem.setOwner(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRepository.saveAndFlush(any(Item.class)))
                .thenThrow(new ItemNotUpdateException("Вещь не была обновлена"));

        final ItemNotUpdateException exception = assertThrows(ItemNotUpdateException.class,
                () -> itemService.updateItem(itemId, new ItemDto(), userId));

        assertThat("Вещь не была обновлена", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRepository);
        verify(itemRepository, times(1)).findById(anyLong());
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(itemRepository, times(1)).saveAndFlush(any(Item.class));
    }

    @Test
    @DisplayName("получены все вещи по тексту, когда вызваны по умолчанию, то получен пустой список")
    void findItems_whenInvokedWithEmptyText_thenReturnedEmptyList() {
        Long userId = 0L;

        List<ItemDto> actualItems = itemService.findItems("", userId, 0, 1);

        assertThat(actualItems, empty());
        verify(itemRepository, never()).search("", PageRequest.of(0, 1));
    }

    @Test
    @DisplayName("получены все вещи по тексту, когда вызваны по умолчанию, то получен пустой список")
    void findItems_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;

        List<ItemDto> actualItems = itemService.findItems("1", userId, 0, 1);

        assertThat(actualItems, empty());
        verify(itemRepository, times(1))
                .search("1", PageRequest.of(0, 1));
    }

    @Test
    @DisplayName("получены все вещи по тексту, когда вызваны, то получен непустой список")
    void findItems_whenInvoked_thenReturnedItemsCollectionInList() {
        Long userId = 0L;
        List<Item> expectedItems = Arrays.asList(new Item(), new Item());
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(expectedItems);

        List<ItemDto> actualItems = itemService.findItems("1", userId, 1, 1);

        assertThat(ItemMapper.INSTANCE.convertItemListToItemDTOList(expectedItems),
                equalTo(actualItems));
        verify(itemRepository, times(1))
                .search("1", PageRequest.of(1, 1));
    }

    @Test
    @DisplayName("сохранен комментарий, когда комментарий валиден, тогда он сохраняется")
    void saveComment_whenCommentValid_thenSavedComment() {
        Comment comment = new Comment();
        CommentDto commentToSave = CommentMapper.INSTANCE.toCommentDto(comment);
        Long userId = 0L;
        User user = new User();
        Long itemId = 0L;
        Item item = new Item();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto actualComment = itemService.saveComment(commentToSave, itemId, userId);

        assertThat(commentToSave, equalTo(actualComment));
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, times(1))
                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
        inOrder.verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("сохранен комментарий, когда вещь не найдена, тогда выбрасывается исключение")
    void saveComment_whenItemNotFound_thenExceptionThrown() {
        CommentDto commentToSave = new CommentDto();
        Long itemId = 0L;
        Long userId = 0L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        final ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.saveComment(commentToSave, itemId, userId));

        assertThat("Вещь с идентификатором 0 не найдена.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, never()).findById(userId);
        inOrder.verify(bookingRepository, never())
                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
        inOrder.verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("сохранен комментарий, когда пользователь не найден, тогда выбрасывается исключение")
    void saveComment_whenUserNotFound_thenExceptionThrown() {
        CommentDto commentToSave = new CommentDto();
        Long itemId = 0L;
        Long userId = 0L;
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.saveComment(commentToSave, itemId, userId));

        assertThat("Пользователь с id = 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, never())
                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
        inOrder.verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("сохранен комментарий, когда комментарий не сохранен, тогда выбрасывается исключение")
    void saveComment_whenCommentNotSaved_thenExceptionThrown() {
        CommentDto commentToSave = new CommentDto();
        Long itemId = 0L;
        Long userId = 0L;

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(0L);
        when(commentRepository.save(any(Comment.class)))
                .thenThrow(new CommentNotSaveException("Комментарий не был создан"));

        final CommentNotSaveException exception = assertThrows(CommentNotSaveException.class,
                () -> itemService.saveComment(commentToSave, itemId, userId));

        assertThat("Комментарий не был создан", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, times(1))
                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
        inOrder.verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("сохранен комментарий, когда комментарий не валиден, тогда выбрасывается исключение")
    void saveComment_whenCommentNotValid_thenExceptionThrown() {
        CommentDto commentToSave = new CommentDto();
        Long itemId = 0L;
        Long userId = 0L;

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(bookingRepository.isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.saveComment(commentToSave, itemId, userId));

        assertThat("Ошибка!  Отзыв может оставить только тот пользователь, который брал эту вещь в аренду, " +
                "и только после окончания срока аренды. Код ошибки: 20002", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(itemRepository, userRepository, bookingRepository, commentRepository);
        inOrder.verify(itemRepository, times(1)).findById(itemId);
        inOrder.verify(userRepository, times(1)).findById(userId);
        inOrder.verify(bookingRepository, times(1))
                .isFindBooking(anyLong(), anyLong(), any(LocalDateTime.class));
        inOrder.verify(commentRepository, never()).save(any(Comment.class));
    }

}