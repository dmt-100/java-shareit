package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String name;

    @NotEmpty(message = "Ошибка! e-mail не может быть пустым.")
    @Email(message = "Ошибка! Неверный e-mail.")
    private String email;

}