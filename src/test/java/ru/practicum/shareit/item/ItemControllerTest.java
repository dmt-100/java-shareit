package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    @DisplayName("получены все вещи пользователя, когда вызваны по умолчанию, то ответ статус ок и пустое тело")
    void getAllItemsByUser_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        ResponseEntity<List<ItemDto>> response = itemController.getAllItemsByUser(userId, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(response.getBody(), empty());
        verify(itemService, times(1)).getAllItemsByUser(userId, 0, 0);
    }

    @Test
    @DisplayName("получены все вещи пользователя, когда вызваны, то ответ статус ок и непустое тело")
    void getAllItemsByUser_whenInvoked_thenResponseStatusOkWithItemsCollectionInBody() {
        Long userId = 0L;
        List<ItemDto> expectedItems = Arrays.asList(new ItemDto());
        when(itemService.getAllItemsByUser(userId, 0, 0)).thenReturn(expectedItems);

        ResponseEntity<List<ItemDto>> response = itemController.getAllItemsByUser(userId, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItems, equalTo(response.getBody()));
        verify(itemService, times(1)).getAllItemsByUser(userId, 0, 0);
    }

    @Test
    @DisplayName("получена вещь по ид, когда вещь найдена, то ответ статус ок, и она возвращается")
    void getItemById_whenItemFound_thenReturnedItem() {
        long itemId = 0L;
        long userId = 0L;
        ItemDto expectedItem = new ItemDto();
        when(itemService.getItemById(itemId, userId)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.getItemById(itemId, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItem, equalTo(response.getBody()));
        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @Test
    @DisplayName("сохранена вещь, когда вещь валидна, то ответ статус ок, и она сохраняется")
    void saveItem_whenItemValid_thenSavedItem() {
        ItemDto expectedItem = new ItemDto();
        long userId = 0L;
        when(itemService.saveItem(expectedItem, userId)).thenReturn(expectedItem);

        ResponseEntity<ItemDto> response = itemController.saveItem(expectedItem, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItem, equalTo(response.getBody()));
        verify(itemService, times(1)).saveItem(expectedItem, userId);
    }

    @Test
    @DisplayName("обновлена вещь, когда вещь валидна, то ответ статус ок, и она обновляется")
    void updateItem_whenItemValid_thenUpdatedItem() {
        Long itemId = 0L;
        Long userId = 0L;
        ItemDto newItem = new ItemDto();
        newItem.setName("2");
        newItem.setDescription("2");
        newItem.setAvailable(true);
        when(itemService.updateItem(itemId, newItem, userId)).thenReturn(newItem);

        ResponseEntity<ItemDto> response = itemController.updateItem(itemId, newItem, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(newItem, equalTo(response.getBody()));
        verify(itemService, times(1)).updateItem(itemId, newItem, userId);
    }

    @Test
    @DisplayName("получены вещи потенциальным арендатором по тексту, когда вызваны по умолчанию, " +
            "то ответ статус ок и пустое тело")
    void findItems_whenInvokedDefault_thenResponseStatusOkWithEmptyBody() {
        Long userId = 0L;
        ResponseEntity<List<ItemDto>> response = itemController.findItems("", userId, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(response.getBody(), empty());
        verify(itemService, times(1)).findItems("", userId, 0, 0);
    }

    @Test
    @DisplayName("получены вещи потенциальным арендатором по тексту, когда вызваны, " +
            "то ответ статус ок и непустое тело")
    void findItems_whenInvoked_thenResponseStatusOkWithItemsCollectionInBody() {
        Long userId = 0L;
        List<ItemDto> expectedItems = Arrays.asList(new ItemDto());
        Mockito.when(itemService.findItems("", userId, 0, 0)).thenReturn(expectedItems);

        ResponseEntity<List<ItemDto>> response = itemController.findItems("", userId, 0, 0);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedItems, equalTo(response.getBody()));
        verify(itemService, times(1)).findItems("", userId, 0, 0);
    }

    @Test
    @DisplayName("сохранен комментарий, когда коммент валиден, то ответ статус ок, и он сохраняется")
    void saveComment_whenCommentValid_thenSavedComment() {
        CommentDto expectedComment = new CommentDto();
        long itemId = 0L;
        long userId = 0L;
        when(itemService.saveComment(expectedComment, itemId, userId)).thenReturn(expectedComment);

        ResponseEntity<CommentDto> response = itemController.saveComment(expectedComment, itemId, userId);

        assertThat(HttpStatus.OK, equalTo(response.getStatusCode()));
        assertThat(expectedComment, equalTo(response.getBody()));
        verify(itemService, times(1)).saveComment(expectedComment, itemId, userId);
    }

}