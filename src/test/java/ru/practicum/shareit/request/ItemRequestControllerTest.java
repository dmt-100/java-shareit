package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestServiceImpl itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны по умолчанию, то ответ статус ок и пустое тело")
    void getAllItemRequestsByUser_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        ResponseEntity<List<ItemRequestDto>> response = itemRequestController
                .getAllItemRequestsByUser(userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(response.getBody(), empty());
        verify(itemRequestService, times(1)).getAllItemRequestsByUser(userId);
    }

    @Test
    @DisplayName("получены все свои запросы вместе с данными об ответах на них, " +
            "когда вызваны, то ответ статус ок и непустое тело")
    void getAllItemRequestsByUser_whenInvoked_thenResponseStatusOkWithItemRequestsCollectionInBody() {
        Long userId = 0L;
        List<ItemRequestDto> expectedItemRequests = Arrays.asList(new ItemRequestDto());
        when(itemRequestService.getAllItemRequestsByUser(userId)).thenReturn(expectedItemRequests);

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController
                .getAllItemRequestsByUser(userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItemRequests, equalTo(response.getBody()));
        verify(itemRequestService, times(1)).getAllItemRequestsByUser(userId);
    }

    @Test
    @DisplayName("получены все запросы, созданные другими пользователями, " +
            "когда вызваны по умолчанию, то ответ статус ок и пустое тело")
    void getAllItemRequestsByOtherUsers_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        ResponseEntity<List<ItemRequestDto>> response = itemRequestController
                .getAllItemRequestsByOtherUsers(userId, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(response.getBody(), empty());
        verify(itemRequestService, times(1))
                .getAllItemRequestsByOtherUsers(userId, 0, 0);

    }

    @Test
    @DisplayName("получены все запросы, созданные другими пользователями, " +
            "когда вызваны, то ответ статус ок и непустое тело")
    void getAllItemRequestsByOtherUsers_whenInvoked_thenResponseStatusOkWithItemRequestsCollectionInBody() {
        Long userId = 0L;
        List<ItemRequestDto> expectedItemRequests = Arrays.asList(new ItemRequestDto());
        when(itemRequestService.getAllItemRequestsByOtherUsers(userId, 0, 0))
                .thenReturn(expectedItemRequests);

        ResponseEntity<List<ItemRequestDto>> response = itemRequestController
                .getAllItemRequestsByOtherUsers(userId, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItemRequests, equalTo(response.getBody()));
        verify(itemRequestService, times(1))
                .getAllItemRequestsByOtherUsers(userId, 0, 0);
    }

    @Test
    @DisplayName("получен запрос по ид, когда запрос найден, то ответ статус ок, и он возвращается")
    void getItemRequestById_whenItemRequestFound_thenReturnedItemRequest() {
        long requestId = 0L;
        long userId = 0L;
        ItemRequestDto expectedItemRequest = new ItemRequestDto();
        when(itemRequestService.getItemRequestById(requestId, userId)).thenReturn(expectedItemRequest);

        ResponseEntity<ItemRequestDto> response = itemRequestController
                .getItemRequestById(requestId, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItemRequest, equalTo(response.getBody()));
        verify(itemRequestService, times(1))
                .getItemRequestById(requestId, userId);
    }

    @Test
    @DisplayName("сохранен запрос, когда запрос валиден, то ответ статус ок, и он сохраняется")
    void saveItemRequest_whenItemRequestValid_thenSavedItemRequest() {
        ItemRequestDto expectedItemRequest = new ItemRequestDto();
        long userId = 0L;
        when(itemRequestService.saveItemRequest(expectedItemRequest, userId))
                .thenReturn(expectedItemRequest);

        ResponseEntity<ItemRequestDto> response = itemRequestController
                .saveItemRequest(expectedItemRequest, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItemRequest, equalTo(response.getBody()));
        verify(itemRequestService, times(1))
                .saveItemRequest(expectedItemRequest, userId);
    }

}