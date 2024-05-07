package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(int requesterId);

    List<ItemRequest> findByRequesterIdNot(int requesterId, Pageable pageable);
}
