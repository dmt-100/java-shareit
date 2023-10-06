package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class User {

    private Long id; // уникальный идентификатор пользователя
    @NonNull
    private String name; // имя или логин пользователя
    @NonNull
    private String email; // адрес электронной почты, уникальный

}