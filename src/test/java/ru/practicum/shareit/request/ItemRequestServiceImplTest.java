package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.exception.ItemRequestNotSaveException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны по умолчанию, то получен пустой список")
    void getAllItemRequestsByUser_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        List<ItemRequestDto> actualItemRequests = itemRequestService.getAllItemRequestsByUser(userId);

        assertThat(actualItemRequests, empty());
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1))
                .findAllByRequestorId(anyLong());
    }

    @Test
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны, то получен непустой список")
    void getAllItemRequestsByUser_whenInvoked_thenReturneItemRequestsCollectionInList() {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = Arrays.asList(new ItemRequest(), new ItemRequest());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(expectedItemRequests);

        List<ItemRequestDto> actualItemRequests = itemRequestService.getAllItemRequestsByUser(userId);

        assertThat(ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(expectedItemRequests),
                equalTo(actualItemRequests));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong());
    }

    @Test
    @DisplayName("получены все запросы, созданные другими пользователями, " +
            "когда вызваны по умолчанию, то получен пустой список")
    void getAllItemRequestsByOtherUsers_whenInvoked_thenReturnedEmptyList() {
        Long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        List<ItemRequestDto> actualItemRequests = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, 0, 1);

        assertThat(actualItemRequests, empty());
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1))
                .findAllByRequestorIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("получены все запросы, созданные другими пользователями, " +
            "когда вызваны, то получен непустой список")
    void getAllItemRequestsByOtherUsers_whenInvoked_thenReturneItemRequestsCollectionInList() {
        Long userId = 0L;
        List<ItemRequest> expectedItemRequests = Arrays.asList(new ItemRequest(), new ItemRequest());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(expectedItemRequests);

        List<ItemRequestDto> actualItemRequests = itemRequestService
                .getAllItemRequestsByOtherUsers(userId, 0, 1);

        assertThat(ItemRequestMapper.INSTANCE.convertItemRequestListToItemRequestDTOList(expectedItemRequests),
                equalTo(actualItemRequests));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1))
                .findAllByRequestorIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("получен запрос по ид, когда запрос найден, тогда он возвращается")
    void getItemRequestById_whenItemRequestFound_thenReturnedUser() {
        long userId = 0L;
        long itemRequestId = 0L;
        ItemRequest expectedItemRequest = new ItemRequest();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(userId)).thenReturn(Optional.of(expectedItemRequest));

        ItemRequestDto actualItemRequest = itemRequestService.getItemRequestById(itemRequestId, userId);

        assertThat(ItemRequestMapper.INSTANCE.toItemRequestDto(expectedItemRequest), equalTo(actualItemRequest));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).findById(itemRequestId);
    }

    @Test
    @DisplayName("получен запрос по ид, когда запрос не найден, " +
            "тогда выбрасывается исключение")
    void getItemRequestById_whenItemRequestNotFound_thenExceptionThrown() {
        long itemRequestId = 0L;
        long userId = 0L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        final ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(itemRequestId, userId));

        assertThat("Запрос с идентификатором 0 не найден.", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("сохранен запрос, когда запрос не валиден, тогда выбрасывается исключение")
    void saveItemRequest_whenItemRequestNotValid_thenExceptionThrown() {
        long userId = 0L;
        ItemRequestDto itemRequestToSave = new ItemRequestDto();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenThrow(new ItemRequestNotSaveException("Запрос не был создан"));

        final ItemRequestNotSaveException exception = assertThrows(ItemRequestNotSaveException.class,
                () -> itemRequestService.saveItemRequest(itemRequestToSave, userId));

        assertThat("Запрос не был создан", equalTo(exception.getMessage()));
        InOrder inOrder = inOrder(userRepository, itemRequestRepository);
        inOrder.verify(userRepository, times(1)).findById(anyLong());
        inOrder.verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

}