package ru.practicum.shareit.booking.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Transactional
    List<Booking> findByBooker(User booker);

    List<Booking> findByBookerAndStartAfter(User booker, LocalDateTime start);

    List<Booking> findByBookerAndEndBefore(User booker, LocalDateTime end);

    List<Booking> findByBookerAndStartLessThanEqualAndEndGreaterThanEqual(User booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerAndStatus(User booker, BookingStatus status);

    List<Booking> findByItem(Item item);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.booker.id = :bookerId AND b.end < :end AND b.status = :status")
    boolean existsByItemIdAndBookerIdAndEndBeforeAndStatus(
            @Param("itemId") Long itemId,
            @Param("bookerId") Long bookerId,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );
}
