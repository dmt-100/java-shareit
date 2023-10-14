package ru.practicum.shareit.user.model;

import javax.persistence.*;

import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name = "users", schema = "public")
@Data
public class User {

    public User() {
    }

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