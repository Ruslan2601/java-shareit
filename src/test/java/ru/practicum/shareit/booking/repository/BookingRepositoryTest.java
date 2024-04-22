package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    void fillingDB() {
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE items ALTER COLUMN item_id RESTART WITH 1;").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE bookings ALTER COLUMN booking_id RESTART WITH 1;").executeUpdate();

        for (int i = 1; i <= 5; ++i) {
            userRepository.save(new User(i, "name " + i, "user" + i + "@email.com"));
        }

        User user = userRepository.findById(1).orElseThrow(() -> new ObjectNotFoundException("test"));
        for (int i = 1; i <= 5; ++i) {
            itemRepository.save(new Item(i, "item name " + i, "description " + i, true, user, null));
        }

        user = userRepository.findById(2).orElseThrow(() -> new ObjectNotFoundException("test"));
        for (int i = 6; i <= 10; ++i) {
            itemRepository.save(new Item(i, "item name " + i, "description " + i, true, user, null));
        }

        Item item = itemRepository.findById(1).orElseThrow(() -> new ObjectNotFoundException("test"));
        for (int i = 1; i <= 3; ++i) {
            LocalDateTime start = LocalDateTime.of(i, 1, 1, 1, 1, 1);
            LocalDateTime end = LocalDateTime.of(1 + i, 1, 1, 1, 1, 1);
            repository.save(new Booking(i, start, end, item, user, Status.APPROVED));
        }

        for (int i = 4; i <= 5; ++i) {
            LocalDateTime start = LocalDateTime.of(3000 + i, 1, 1, 1, 1, 1);
            LocalDateTime end = LocalDateTime.of(4000 + i, 1, 1, 1, 1, 1);
            repository.save(new Booking(i, start, end, item, user, Status.APPROVED));
        }
    }

    @Test
    void findFirstByItemIdAndItem_OwnerIdAndStartBeforeAndStatusOrderByStartDesc() {
        Booking lastBooking = repository
                .findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                        1, 1, LocalDateTime.now(), Status.APPROVED);

        assertEquals(3, lastBooking.getId());
        assertEquals(3, lastBooking.getStart().getYear());
        assertEquals(4, lastBooking.getEnd().getYear());
    }

    @Test
    void findFirstByItemIdAndItem_OwnerIdAndStartAfterAndStatusOrderByStartAsc() {
        Booking nextBooking = repository
                .findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                        1, 1, LocalDateTime.now(), Status.APPROVED);

        assertEquals(4, nextBooking.getId());
        assertEquals(3004, nextBooking.getStart().getYear());
        assertEquals(4004, nextBooking.getEnd().getYear());
    }
}