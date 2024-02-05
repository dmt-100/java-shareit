package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// класс, отвечающий за запрос вещи
@Entity
@Table(name = "requests", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  уникальный идентификатор запроса

    @Column(nullable = false)
    private String description; // текст запроса, содержащий описание требуемой вещи

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor; // пользователь, создавший запрос

    @Column
    private Instant created; // = Instant.now(); // дата и время создания запроса

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    private Set<Item> items = new HashSet<>();

}