package ru.practicum.shareit.item.model;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import lombok.NonNull;
import lombok.Data;

@Data
public class Item {

    private Long id; // уникальный идентификатор вещи
    @NonNull
    private String name; // краткое название
    @NonNull
    private String description; // развёрнутое описание
    @NonNull
    private Boolean available; // статус о том, доступна или нет вещь для аренды
    private final User owner; // владелец вещи
    private ItemRequest request;
    // если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос

    public Boolean isAvailable() {
        return getAvailable();
    }

}