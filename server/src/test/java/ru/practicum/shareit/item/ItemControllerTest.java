package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.RequestItem;
import ru.practicum.shareit.request.repository.RequestItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemControllerTest {
    ObjectMapper mapper;
    MockMvc mvc;

    @MockBean
    ItemService itemService;
    @MockBean
    RequestItemRepository requestItemRepository;

    static ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("pen")
                .description("smth")
                .available(Boolean.TRUE)
                .requestId(1L)
                .build();
    }

    static User createOwner() {
        return User.builder()
                .id(1)
                .name("Bob")
                .email("owner@owner.com")
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

    static Comment createComment(User author, Item item) {
        return Comment.builder()
                .id(1)
                .text("smth")
                .author(author)
                .created(LocalDateTime.now())
                .item(item)
                .build();
    }

    static CommentRequestDto createCommentDto() {
        return CommentRequestDto.builder()
                .id(1L)
                .text("smth")
                .build();
    }

    @Test
    @SneakyThrows
    void addItemWithValidData() {
        User owner = createOwner();
        Item item = createItem(owner);
        ItemDto dto = createItemDto();
        when(itemService.addItem(any(), anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("pen"),
                        jsonPath("$.description").value("smth"),
                        jsonPath("$.available").value("true")
                );
    }

    @Test
    @SneakyThrows
    void addItemWithoutUserId() {
        User owner = createOwner();
        Item item = createItem(owner);
        ItemDto dto = createItemDto();
        when(itemService.addItem(any(), anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).addItem(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void getItemByIdWithValidData() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(item);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value("1"),
                        jsonPath("$.name").value("pen"),
                        jsonPath("$.description").value("smth"),
                        jsonPath("$.available").value("true")
                );
    }

    @Test
    @SneakyThrows
    void getItemByIdWithNullPath() {
        User owner = createOwner();
        Item item = createItem(owner);
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(item);
        mvc.perform(get("/items/null")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(
                        status().is5xxServerError());
        verify(itemService, never()).addItem(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateItemWithValidData() {
        User owner = createOwner();
        Item item = createItem(owner);
        ItemDto dto = createItemDto();
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("pen"),
                        jsonPath("$.description").value("smth"),
                        jsonPath("$.available").value("true")
                );
    }

    @Test
    @SneakyThrows
    void updateItemWithoutUserId() {
        User owner = createOwner();
        Item item = createItem(owner);
        ItemDto dto = createItemDto();
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(item);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(patch("/items/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).updateItem(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getOwnersItemsWithValidData() {
        User owner = createOwner();
        List<Item> items = List.of(createItem(owner));
        when(itemService.getOwnerItems(anyLong()))
                .thenReturn(items);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", Matchers.is(items.get(0).getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getOwnersItemsWithoutUserId() {
        User owner = createOwner();
        List<Item> items = List.of(createItem(owner));
        when(itemService.getOwnerItems(anyLong()))
                .thenReturn(items);
        mvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).getOwnerItems(anyLong());
    }

    @Test
    @SneakyThrows
    void findItemByNameOrDescriptionWithValidData() {
        User owner = createOwner();
        List<Item> items = List.of(createItem(owner));
        when(itemService.getItemByNameOrDescription(anyString()))
                .thenReturn(items);
        mvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "smth"))
                .andExpectAll(status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id", Matchers.is(items.get(0).getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void findItemByNameOrDescriptionWithoutText() {
        User owner = createOwner();
        List<Item> items = List.of(createItem(owner));
        when(itemService.getItemByNameOrDescription(anyString()))
                .thenReturn(items);
        mvc.perform(get("/items/search")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
        verify(itemService, never()).getItemByNameOrDescription(anyString());
    }

    @Test
    @SneakyThrows
    void addCommentWithValidData() {
        User author = createOwner();
        Item item = createItem(author);
        Comment comment = createComment(author, item);
        CommentRequestDto dto = createCommentDto();
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.text").value("smth")
                );
    }

    @Test
    @SneakyThrows
    void addCommentWithoutUserId() {
        User author = createOwner();
        Item item = createItem(author);
        Comment comment = createComment(author, item);
        CommentRequestDto dto = createCommentDto();
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(comment);
        String json = mapper.writeValueAsString(dto);
        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(status().is5xxServerError());
        verify(itemService, never()).addComment(anyLong(), anyLong(), any());
    }
}
