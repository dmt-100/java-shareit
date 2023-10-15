package ru.practicum.shareit.item.model;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "items", schema = "public")
@Data
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор вещи

    @Column(nullable = false)
    private String name; // краткое название

    @Column(nullable = false)
    private String description; // развёрнутое описание

    @Column(name = "is_available", nullable = false)
    private Boolean available; // статус о том, доступна или нет вещь для аренды

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner; // владелец вещи

    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
    // если вещь была создана по запросу другого пользователя,
    // то в этом поле будет храниться ссылка на соответствующий запрос

}