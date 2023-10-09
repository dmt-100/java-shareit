package ru.practicum.shareit.request;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-item-requests.
 */

@Data
public class ItemRequest {

    private Long id;
    @NonNull
    private final String description;
    @NonNull
    private final User requestor;
    @NonNull
    private final LocalDateTime created;

}