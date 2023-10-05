package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class User {

    private Long id; // уникальный идентификатор пользователя
    @NonNull
    private String name; // имя или логин пользователя
    @NonNull
    @NotEmpty
    @Email(message = "Ошибка! Неверный e-mail.")
    private String email; // адрес электронной почты, уникальный

}