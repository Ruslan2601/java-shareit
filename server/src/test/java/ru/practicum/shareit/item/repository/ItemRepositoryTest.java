package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    void fillingDB() {
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE items ALTER COLUMN item_id RESTART WITH 1;").executeUpdate();

        for (int i = 1; i <= 5; ++i) {
            userRepository.save(new User(i, "name " + i, "user" + i + "@email.com"));
        }

        User user = userRepository.findById(1).orElseThrow(() -> new ObjectNotFoundException("test"));
        for (int i = 1; i <= 5; ++i) {
            repository.save(new Item(i, "item name " + i, "description " + i, true, user, null));
        }

        user = userRepository.findById(2).orElseThrow(() -> new ObjectNotFoundException("test"));
        for (int i = 6; i <= 10; ++i) {
            repository.save(new Item(i, "item name " + i, "description " + i, true, user, null));
        }
    }

    @Test
    void search_byName() {
        String text = "item name 4";

        List<Item> items = repository.search(text, PageRequest.of(0, 10));

        assertEquals(1, items.size());
        assertEquals(4, items.get(0).getId());
        assertEquals(1, items.get(0).getOwner().getId());
        assertEquals(text, items.get(0).getName());
    }

    @Test
    void search_byDescription() {
        String text = "description 4";

        List<Item> items = repository.search(text, PageRequest.of(0, 10));

        assertEquals(1, items.size());
        assertEquals(4, items.get(0).getId());
        assertEquals(1, items.get(0).getOwner().getId());
        assertEquals(text, items.get(0).getDescription());
    }

    @Test
    void search_whenItemIsNotAvailable() {
        String text = "description 4";

        Item item = repository.findById(4).orElseThrow(() -> new ObjectNotFoundException("test"));
        item.setAvailable(false);
        repository.save(item);

        List<Item> items = repository.search(text, PageRequest.of(0, 10));

        assertEquals(0, items.size());
    }

    @Test
    void search_whenItemNotFound() {
        String text = "name 55";

        List<Item> items = repository.search(text, PageRequest.of(0, 10));

        assertEquals(0, items.size());
    }
}