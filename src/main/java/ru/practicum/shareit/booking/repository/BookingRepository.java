package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByItem_IdAndBooker_Id(Long itemId,  Long bookerId);

    List<Booking> findAllByBooker_Id(Long bookerId);

    List<Booking> findAllByItem_Id(Long itemId);

    List<Booking> findAllByItem_IdIn(List<Long> itemsId);
}
