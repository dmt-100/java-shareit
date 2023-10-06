package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class User {

    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String email;

}