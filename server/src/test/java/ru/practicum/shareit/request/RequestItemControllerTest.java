package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.service.RequestItemService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestItemControllerTest {
    ObjectMapper mapper;
    MockMvc mvc;

    @MockBean
    RequestItemService requestItemService;

    static User createOwner() {
        return User.builder()
                .id(1)
                .name("Bob")
                .email("owner@owner.com")
                .build();
    }

    static User createRequester() {
        return User.builder()
                .id(2)
                .name("Tim")
                .email("user@requester.com")
                .build();
    }

    static Item createItem(User owner) {
        return Item.builder()
                .id(1)
                .name("pen")
                .description("smth")
                .owner(owner)
                .available(Boolean.TRUE)
                .comments(Collections.emptyList())
                .request(RequestItem.builder().id(1).build())
                .build();
    }

    static RequestItem createRequest(User requester, Item item) {
        return RequestItem.builder()
                .id(1)
                .created(LocalDateTime.now())
                .items(List.of(item))
                .description("smth")
                .requester(requester)
                .build();
    }

    static RequestItemDto createDto() {
        return RequestItemDto.builder()
                .id(1)
                .created(LocalDateTime.now())
                .description("smth")
                .build();
    }

    @Test
    @SneakyThrows
    void addRequestWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        RequestItemDto dto = createDto();
        when(requestItemService.addRequest(anyLong(), any()))
                .thenReturn(requestItem);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.description").value(requestItem.getDescription()));
    }

    @Test
    @SneakyThrows
    void addRequestWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        RequestItemDto dto = createDto();
        when(requestItemService.addRequest(anyLong(), any()))
                .thenReturn(requestItem);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(requestItemService, never()).addRequest(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void getOwnerRequestsWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(requestItemService.getOwnerRequests(anyLong()))
                .thenReturn(List.of(requestItem));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requester.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].description").value(requestItem.getDescription()));
    }

    @Test
    @SneakyThrows
    void getOwnerRequestsWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(requestItemService.getOwnerRequests(anyLong()))
                .thenReturn(List.of(requestItem));
        mvc.perform(get("/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is5xxServerError());
        verify(requestItemService, never()).getOwnerRequests(anyLong());
    }

    @Test
    @SneakyThrows
    void getAllRequestsWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(requestItemService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestItem));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requester.getId())
                        .param("from", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id").value(1),
                        jsonPath("$[0].description").value(requestItem.getDescription()));
    }

    @Test
    @SneakyThrows
    void getAllRequestsWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(requestItemService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestItem));
        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is5xxServerError());
        verify(requestItemService, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getRequestByIdWithValidData() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(requestItemService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestItem);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", requester.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.description").value(requestItem.getDescription()));
    }

    @Test
    @SneakyThrows
    void getRequestByIdWithoutUserId() {
        User owner = createOwner();
        User requester = createRequester();
        Item item = createItem(owner);
        RequestItem requestItem = createRequest(requester, item);
        when(requestItemService.getRequestById(anyLong(), anyLong()))
                .thenReturn(requestItem);
        mvc.perform(get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().is5xxServerError());
        verify(requestItemService, never()).getRequestById(anyLong(), anyLong());
    }
}
