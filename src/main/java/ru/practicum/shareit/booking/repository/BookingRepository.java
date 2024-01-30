package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Integer bookerID);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerID, LocalDateTime start, LocalDateTime before);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerID, LocalDateTime before);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Integer bookerID, LocalDateTime before);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Integer bookerID, Status status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(User itemOwner);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(User itemOwner, LocalDateTime start, LocalDateTime before);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(User itemOwner, LocalDateTime before);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(User itemOwner, LocalDateTime before);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User itemOwner, Status status);

    Booking findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(Integer itemId, Integer bookerID, LocalDateTime end, Status status);

    Booking findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(Integer itemId, Integer bookerID, LocalDateTime start, Status status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndBefore(Integer itemId, Integer bookerID, LocalDateTime end);
}
