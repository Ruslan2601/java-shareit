package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(Integer id, Pageable pageable);

    List<Item> findByRequestIdOrderByRequestCreatedDesc(int requestId);

    @Query(value = "SELECT * FROM items AS i " +
            "WHERE i.is_available = TRUE " +
            "AND (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%')))",
            nativeQuery = true)
    List<Item> search(@Param("text") String text, Pageable pageable);
}
