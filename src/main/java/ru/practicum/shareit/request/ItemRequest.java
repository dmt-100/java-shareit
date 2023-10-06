package ru.practicum.shareit.request;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-item-requests.
 */

// класс, отвечающий за запрос вещи
@Data
public class ItemRequest {

    private Long id; //  уникальный идентификатор запроса
    @NonNull
    private final String description; // текст запроса, содержащий описание требуемой вещи
    @NonNull
    private final User requestor; // пользователь, создавший запрос
    @NonNull
    private final LocalDateTime created; // дата и время создания запроса

}