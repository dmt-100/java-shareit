package ru.practicum.shareit.user.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validations.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class UserDto {
    private Long id;
    @NotEmpty(message = "Имя пользователя не должно быть пустым", groups = Create.class)
    private String name;
    @NotNull(groups = Create.class)
    @Email(groups = Create.class)
    private String email;
}
