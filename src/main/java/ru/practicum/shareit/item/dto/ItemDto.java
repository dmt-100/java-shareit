package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class ItemDto {

    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private Boolean available;
    private final Long request;

}