package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.request.service.RequestItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.RequestItemControllerTest.*;

@ExtendWith(MockitoExtension.class)
public class RequestItemServiceMockTest {
    @Mock
    UserRepository userRepository;
    @Mock
    RequestItemRepository requestItemRepository;
    @InjectMocks
    RequestItemServiceImpl requestItemService;

    @Test
    void addRequestWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requester));
        when(requestItemRepository.save(any()))
                .thenReturn(requestItem);
        RequestItem result = requestItemService.addRequest(requester.getId(), requestItem);
        assertThat(result.getId(), equalTo(requestItem.getId()));
    }

    @Test
    void addRequestWithoutRequesterEntity() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestItemService.addRequest(requester.getId(), requestItem));
        assertThat(exception.getMessage(), equalTo("No such owner"));
        verify(requestItemRepository, never()).save(any());
    }

    @Test
    void getOwnerRequestsWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(userRepository.existsById(anyLong()))
                .thenReturn(Boolean.TRUE);
        when(requestItemRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(requestItem));
        List<RequestItem> result = requestItemService.getOwnerRequests(requester.getId());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(requestItem.getId()));
    }

    @Test
    void getOwnerRequestsWithoutRequesterEntity() {
        User requester = createRequester();
        when(userRepository.existsById(anyLong()))
                .thenReturn(Boolean.FALSE);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestItemService.getOwnerRequests(requester.getId()));
        assertThat(exception.getMessage(), equalTo("No such owner"));
        verify(requestItemRepository, never()).findAllByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAllRequestsWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(userRepository.existsById(anyLong()))
                .thenReturn(Boolean.TRUE);
        when(requestItemRepository.findRequestPages(any(), anyLong()))
                .thenReturn(List.of(requestItem));
        List<RequestItem> result = requestItemService.getAllRequests(requester.getId(), 0, 3);
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(requestItem.getId()));
    }

    @Test
    void getAllRequestsWithoutRequesterEntity() {
        User requester = createRequester();
        when(userRepository.existsById(anyLong()))
                .thenReturn(Boolean.FALSE);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestItemService.getAllRequests(requester.getId(), 0, 3));
        assertThat(exception.getMessage(), equalTo("No such owner"));
        verify(requestItemRepository, never()).findRequestPages(any(), anyLong());
    }

    @Test
    void getRequestByIdWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(userRepository.existsById(anyLong()))
                .thenReturn(Boolean.TRUE);
        when(requestItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestItem));
        RequestItem result = requestItemService.getRequestById(requester.getId(), requestItem.getId());
        assertThat(result.getId(), equalTo(requestItem.getId()));
    }

    @Test
    void getRequestByIdWithoutRequestEntity() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(userRepository.existsById(anyLong()))
                .thenReturn(Boolean.TRUE);
        when(requestItemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestItemService.getRequestById(requester.getId(), requestItem.getId()));
        assertThat(exception.getMessage(), equalTo("No such request"));
    }

    @Test
    void getRequestByIdWithoutRequesterEntity() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(userRepository.existsById(anyLong()))
                .thenReturn(Boolean.FALSE);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestItemService.getRequestById(requester.getId(), requestItem.getId()));
        assertThat(exception.getMessage(), equalTo("No such owner"));
    }
}
