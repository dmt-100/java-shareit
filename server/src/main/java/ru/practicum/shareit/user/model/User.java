package ru.practicum.shareit.user.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор пользователя

    @Column(name = "name", nullable = false)
    private String name; // имя или логин пользователя

    @Column(name = "email", nullable = false)
    private String email; // адрес электронной почты, уникальный

}