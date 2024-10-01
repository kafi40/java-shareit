package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.DateTimeAlreadyTakenException;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto get(Long id) {
        return bookingRepository.findOne(id)
                .map(BookingMapper::mapTo)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + id + " не найден"));
    }

    @Override
    public List<BookingDto> getAll() {
        return bookingRepository.findAll().stream()
                .map(BookingMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto create(Long userId, BookingDto request) {
        checkTimeIntersection(request);
        Booking booking = bookingRepository.save(BookingMapper.mapFrom(request));
        return BookingMapper.mapTo(booking);
    }

    @Override
    public BookingDto patch(Long id, Long userId, BookingDto request) {
        if (!request.isHasPermissionToField(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        if (request.getStart() != null || request.getEnd() != null) {
            checkTimeIntersection(request);
        }

        Booking booking = bookingRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + id + " не найден"));

        BookingMapper.updateField(booking, request);
        return BookingMapper.mapTo(booking);
    }

    @Override
    public void delete(Long id, Long userId) {
        Booking booking = bookingRepository.findOne(id)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + id + " не найден"));

        if (!booking.getBooker().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        bookingRepository.delete(id);
    }

    private void checkTimeIntersection(BookingDto request) {
        bookingRepository.findAll().stream()
                .filter(booking -> booking.getItem().getId().equals(request.getItem().getId()))
                .peek(booking -> {
                    if (booking.isCross(request.getStart(), request.getEnd())) {
                        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                            throw new DateTimeAlreadyTakenException("Выбранное время уже забронировано");
                        }
                    }
                })
                .close();
    }
}
