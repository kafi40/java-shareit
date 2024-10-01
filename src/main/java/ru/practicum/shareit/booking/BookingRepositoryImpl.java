package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.base.BaseRepositoryImpl;
import ru.practicum.shareit.booking.model.Booking;

@Repository
public class BookingRepositoryImpl extends BaseRepositoryImpl<Booking> implements BookingRepository {
}
