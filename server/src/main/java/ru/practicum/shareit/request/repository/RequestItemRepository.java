package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.RequestItem;

import java.util.List;

public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {
    @Query("select r from RequestItem as r  " +
            "join r.requester as u " +
            "where u.id = ?1 " +
            "order by r.created desc")
    List<RequestItem> findAllByRequesterIdOrderByCreatedDesc(long ownerId);

    @Query("select r from RequestItem as r " +
            "where r.requester.id != ?1 " +
            "order by r.created desc")
    @EntityGraph(attributePaths = "items")
    List<RequestItem> findRequestPages(Pageable page, long ownerId);
}
