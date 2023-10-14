package ru.practicum.shareit.item.model;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import lombok.NonNull;
import lombok.Data;

@Entity
@Table(name = "items", schema = "public")
@Data
public class Item {

    public Item() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор вещи

    @NonNull
    @Column(nullable = false)
    private String name; // краткое название

    @NonNull
    @Column(nullable = false)
    private String description; // развёрнутое описание

    @NonNull
    @Column(name = "is_available", nullable = false)
    private Boolean available; // статус о том, доступна или нет вещь для аренды

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // владелец вещи

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "request_id")
    private Set<ItemRequest> requests = new HashSet<>();
    // если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос

}