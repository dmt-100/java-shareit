package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ItemServiceImpl itemService;

    private final Long userId = 0L;
    private final UserDto userDto = new UserDto();
    private final ItemDto itemDto = new ItemDto();
    private final ItemDto itemDto2 = new ItemDto();

    @BeforeEach
    public void addItems() {
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("mail@mail.ru");

        itemDto.setId(1L);
        itemDto.setName("item 1");
        itemDto.setDescription("description 1");
        itemDto.setAvailable(true);
        itemDto2.setId(2L);
        itemDto2.setName("item 2");
        itemDto2.setDescription("description 2");
        itemDto2.setAvailable(true);
    }

    @SneakyThrows
    @Test
    @DisplayName("получены все вещи пользователя, когда вызваны, " +
            "то ответ статус ок и список вещей")
    void getAllItemsByUser_whenInvoked_thenResponseStatusOkWithItemsCollectionInBody() {
        List<ItemDto> items = Arrays.asList(itemDto, itemDto2);
        when(itemService.getAllItemsByUser(anyLong(), anyInt(), anyInt())).thenReturn(items);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(items), equalTo(result));
        verify(itemService, times(1)).getAllItemsByUser(userId, 0, 5);
    }

    @SneakyThrows
    @Test
    @DisplayName("получены все вещи пользователя, когда вызваны без пользователя, " +
            "то ответ статус бед реквест")
    void getAllItemsByUser_whenInvoked_thenResponseStatusBadRequest() {
        String result = mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"error\":\"Required request header 'X-Sharer-User-Id' for method parameter " +
                "type Long is not present\"}", equalTo(result));
        verify(itemService, never()).getAllItemsByUser(userId, 0, 5);
    }

    @SneakyThrows
    @Test
    @DisplayName("получена вещь по ид, когда вещь найдена, " +
            "то ответ статус ок, и она возвращается")
    void getItemById_whenItemFound_thenReturnedItem() {
        long itemId = 0L;
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(itemDto), equalTo(result));
        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранена вещь, когда вещь валидна, " +
            "то ответ статус ок, и она сохраняется")
    void saveItem_whenItemValid_thenSavedItem() {
        when(itemService.saveItem(any(ItemDto.class), anyLong())).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(itemDto), equalTo(result));
        verify(itemService, times(1)).saveItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранена вещь, когда вещь невалидна, то ответ статус бед реквест")
    void saveItem_whenItemNotValid_thenSavedItem() {
        itemDto.setName("");

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"errorResponses\":[{\"error\":\"Ошибка! Краткое название вещи не может быть пустым.\"}]}",
                equalTo(result));
        verify(itemService, never()).saveItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("обновлена вещь, когда вещь валидна, " +
            "то ответ статус ок, и она обновляется")
    void updateItem_whenItemValid_thenUpdatedItem() {
        long itemId = 0L;
        when(itemService.updateItem(anyLong(), any(ItemDto.class), anyLong())).thenReturn(itemDto2);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(itemDto2), equalTo(result));
        verify(itemService, times(1)).updateItem(itemId, itemDto2, userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("получены все вещи по тексту, когда вызваны, " +
            "то ответ статус ок и список вещей")
    void findItems_whenInvoked_thenResponseStatusOkWithItemsCollectionInBody() {
        List<ItemDto> items = Arrays.asList(itemDto, itemDto2);
        when(itemService.findItems(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", "текст")
                        .param("from", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(items), equalTo(result));
        verify(itemService, times(1)).findItems("текст", userId, 0, 10);
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранен комментарий, когда комментарий валиден, " +
            "то ответ статус ок, и он сохраняется")
    void saveComment_whenCommentValid_thenSavedComment() {
        long itemId = 0L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("text 1");

        when(itemService.saveComment(any(CommentDto.class), anyLong(), anyLong())).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(objectMapper.writeValueAsString(commentDto), equalTo(result));
        verify(itemService, times(1)).saveComment(commentDto, itemId, userId);
    }

    @SneakyThrows
    @Test
    @DisplayName("сохранен комментарий, когда комментарий не валиден, то ответ статус бед реквест")
    void saveComment_whenCommentNotValid_thenExceptionThrown() {
        long itemId = 0L;
        CommentDto commentDto = new CommentDto();

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat("{\"errorResponses\":[{\"error\":\"Ошибка! Текст комментария не может быть пустым.\"}]}",
                equalTo(result));
        verify(itemService, never()).saveComment(commentDto, itemId, userId);
    }

}