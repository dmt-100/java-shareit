package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@Data
@Builder
public class UserDto {
    long id;
    @NotBlank(groups = {NewUser.class})
    String name;
    @Email(groups = {NewUser.class, UpdateUser.class})
    @NotBlank(groups = {NewUser.class})
    String email;

    public interface NewUser {
    }

    public interface UpdateUser {
    }
}
