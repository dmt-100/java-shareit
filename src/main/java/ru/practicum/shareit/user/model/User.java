package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор пользователя

    @NonNull
    @Column(name = "name", nullable = false)
    private String name; // имя или логин пользователя

    @NonNull
    @Column(name = "email", nullable = false)
    private String email; // адрес электронной почты, уникальный

}