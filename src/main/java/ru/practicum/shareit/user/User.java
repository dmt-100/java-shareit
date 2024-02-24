package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "Необходимо указать имя пользователя")
    private String name;

    @Column(unique = true)
    @NotNull
    @Email(message = "Невалидный email")
    private String email;
}
