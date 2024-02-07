package ru.practicum.shareit;

import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

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
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;

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
        verify(itemClient, never()).getAllItemsByUser(userId, 0, 5);
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
        verify(itemClient, never()).saveItem(userId, itemDto);
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
        verify(itemClient, never()).saveComment(userId, itemId, commentDto);
    }

}