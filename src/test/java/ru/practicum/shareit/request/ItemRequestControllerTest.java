package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.dto.ItemRequestOutWithItemsDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestsService itemRequestsService;

    private ItemRequestOutDto itemRequestOutDto;
    private ItemRequestInDto itemRequestInDto;
    private ItemRequestOutWithItemsDto itemRequestOutWithItemsDto;

    @BeforeEach
    void setUp() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequester(createUser());
        itemRequest.setDescription("нужен пылесос");
        itemRequest.setId(1L);

        itemRequestOutDto = ItemRequestMapper.mapToItemRequestOutDto(itemRequest);
        itemRequestInDto = ItemRequestMapper.mapToItemRequestInDto(itemRequest);
        itemRequestOutWithItemsDto = ItemRequestMapper.mapToItemRequestOutWithItemsDto(itemRequest, List.of(createItemDto()));
    }

    private User createUser() {
        User user = new User();
        user.setEmail("akhraa1@yandex.ru");
        user.setId(1L);
        user.setName("Akhra");
        return user;
    }

    private ItemDto createItemDto() {
        User user = createUser();

        Item item = new Item();
        item.setRequestId(null);
        item.setId(1L);
        item.setName("Дрель");
        item.setAvailable(true);
        item.setDescription("мощная");
        item.setOwner(user);
        return ItemMapper.toItemDto(item);
    }

    @SneakyThrows
    @Test
    void addRequest_whenКуйгуыеIsNotValid_thenMethodArgumentNotValidExceptionThrown() {
        //given
        itemRequestInDto.setDescription(null);
        //when
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
        verify(itemRequestsService, never()).addRequest(any());
    }

    @SneakyThrows
    @Test
    void addRequest() {
        when(itemRequestsService.addRequest(any()))
                .thenReturn(itemRequestOutDto);
        //when
        String savedRequest = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(itemRequestOutDto), equalTo(savedRequest));
    }

    @SneakyThrows
    @Test
    void getAllUserRequests() {
        when(itemRequestsService.getAllUserRequests(anyLong()))
                .thenReturn(List.of(itemRequestOutWithItemsDto));
        //when
        String requests = mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(List.of(itemRequestOutWithItemsDto)), equalTo(requests));
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(itemRequestsService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestOutWithItemsDto));
        //when
        String requests = mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(List.of(itemRequestOutWithItemsDto)), equalTo(requests));
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(itemRequestsService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestOutWithItemsDto);
        //when
        String request = mvc.perform(get("/requests/{requestId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(mapper.writeValueAsString(itemRequestOutWithItemsDto), equalTo(request));
    }

    @SneakyThrows
    @Test
    void getRequestById_whenIllegalId_thenMethodArgumentTypeMismatchExceptionThrown() {
        //when
        mvc.perform(get("/requests/{requestId}", "id")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }
}