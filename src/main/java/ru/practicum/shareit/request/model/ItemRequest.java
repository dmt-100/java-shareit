package ru.practicum.shareit.request.model;

import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */

// класс, отвечающий за запрос вещи
@Entity
@Table(name = "requests", schema = "public")
@Data
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  уникальный идентификатор запроса

    @NonNull
    @Column(nullable = false)
    private final String description; // текст запроса, содержащий описание требуемой вещи

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private final User requestor; // пользователь, создавший запрос

    @Column
    private final Instant created = Instant.now(); // дата и время создания запроса

}