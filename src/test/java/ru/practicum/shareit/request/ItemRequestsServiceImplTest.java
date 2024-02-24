package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ContentNotFountException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemRequestOutWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestsServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestsServiceImpl itemRequestsService;

    private User createUser() {
        User user = new User();
        user.setEmail("akhraa1@yandex.ru");
        user.setId(1L);
        user.setName("Akhra");
        return user;
    }

    private Item createItem() {
        User owner = createUser();
        owner.setId(2L);

        Item item = new Item();
        item.setRequestId(1L);
        item.setId(1L);
        item.setName("Дрель");
        item.setAvailable(true);
        item.setDescription("мощная");
        item.setOwner(owner);
        return item;
    }

    private ItemRequest createRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(createUser());
        itemRequest.setDescription("нужен пылесос");
        itemRequest.setId(1L);
        return itemRequest;
    }

    @Test
    void addRequest_whenUserNotFound_thenContentNotFountExceptionThrown() {
        //given
        ItemRequest itemRequest = createRequest();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when
        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemRequestsService.addRequest(ItemRequestMapper.mapToItemRequestInDto(itemRequest)));
        //then
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void addRequest_whenUserFound_thenReturnSavedRequest() {
        //given
        ItemRequest itemRequest = createRequest();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest.getRequester()));
        when(itemRequestRepository.save(any())).thenReturn((itemRequest));
        //when
        ItemRequestOutDto savedRequest = itemRequestsService.addRequest(ItemRequestMapper.mapToItemRequestInDto(itemRequest));
        //then
        verify(itemRequestRepository, times(1)).save(any());
        assertThat(savedRequest.getId(), equalTo(itemRequest.getId()));
    }

    @Test
    void getAllUserRequests_whenUserNotFound_thenContentNotFountExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemRequestsService.getAllUserRequests(1L));
    }

    @Test
    void getAllUserRequests_whenUserFound_thenContentNotFountExceptionThrown() {
        //given
        ItemRequest itemRequest1 = createRequest();
        ItemRequest itemRequest2 = createRequest();
        itemRequest2.setId(2L);

        Item item1 = createItem();
        Item item2 = createItem();
        item2.setId(2L);
        item2.setRequestId(2L);

        Item item3 = createItem();
        item3.setId(3L);
        item3.setRequestId(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest1.getRequester()));
        when(itemRequestRepository.findByRequesterId(anyLong(), any())).thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(new ArrayList<>(List.of(item1, item2, item3)));
        //when
        List<ItemRequestOutWithItemsDto> userRequests = itemRequestsService.getAllUserRequests(1L);
        //then
        assertThat(userRequests.size(), equalTo(2));
        assertThat(userRequests.get(0).getItems().size(), equalTo(2));
        assertThat(userRequests.get(1).getItems().size(), equalTo(1));
    }

    @Test
    void getAllRequests_whenUserNotFound_thenContentNotFountExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemRequestsService.getAllRequests(1L, 0, 10));
    }

    @Test
    void getAllRequests_whenUserFound_thenContentNotFountExceptionThrown() {
        //given
        ItemRequest itemRequest1 = createRequest();
        ItemRequest itemRequest2 = createRequest();
        itemRequest2.setId(2L);
        itemRequest1.getRequester().setId(2L);

        Item item = createItem();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest2.getRequester()));
        when(itemRequestRepository.findByRequesterIdNot(anyLong(), any())).thenReturn(List.of(itemRequest1));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(new ArrayList<>(List.of(item)));
        //when
        List<ItemRequestOutWithItemsDto> userRequests = itemRequestsService.getAllRequests(1L, 0, 10);
        //then
        assertThat(userRequests.size(), equalTo(1));
        assertThat(userRequests.get(0).getItems().size(), equalTo(1));
    }

    @Test
    void getRequestById_whenUserNorFound_thenContentNotFountExceptionThrown() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ContentNotFountException.class,
                () -> itemRequestsService.getRequestById(1L, 1L));
    }

    @Test
    void getRequestById_whenRequestNorFound_thenContentNotFountExceptionThrown() {
        //given
        ItemRequest itemRequest = createRequest();

        Item item1 = createItem();
        Item item2 = createItem();
        item2.setId(2L);

        Item item3 = createItem();
        item3.setId(3L);
        item3.setRequestId(2L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item1, item2));
        //when
        ItemRequestOutWithItemsDto itemRequestOutWithItemsDto = itemRequestsService.getRequestById(1L, 1L);
        //then
        assertThat(itemRequestOutWithItemsDto.getItems().size(), equalTo(2));

    }
}