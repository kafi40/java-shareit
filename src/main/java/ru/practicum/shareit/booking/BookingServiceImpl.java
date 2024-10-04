package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingModify;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.DateTimeValueInvalid;
import ru.practicum.shareit.exception.ItemIsUnavailableException;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse get(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toBookingResponse)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + id + " не найден"));
    }

    @Override
    public List<BookingResponse> getAll() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookingMapper.toBookingResponseList(bookings);
    }

    @Override
    public BookingResponse create(Long userId, BookingModify request) {
//        checkTimeIntersection(request);
        if (!request.getStart().isBefore(request.getEnd())) {
            throw new DateTimeValueInvalid("Некорректно заданы значения начала и окончания бронирования");
        }
        request.setStatus(BookingStatus.WAITING);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с ID = " + request.getItemId() + " не найден"));
        if (!item.getAvailable()) {
            throw new ItemIsUnavailableException("Предмет недоступен");
        }
        Booking booking = bookingMapper.toBooking(request);
        booking.setBooker(booker);
        booking.setItem(item);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse patch(Long id, Long userId, BookingModify request) {
//        if (request.getStart() != null || request.getEnd() != null) {
//            checkTimeIntersection(request);
//        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID = " + id + " не найдено"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (request.getStart() != null && userId.equals(bookerId)) {
            booking.setStart(request.getStart());
        }
        if (request.getEnd() != null && userId.equals(bookerId)) {
            booking.setEnd(request.getEnd());
        }
        if (request.getStatus() != null) {
            if (userId.equals(bookerId)) {
                if (request.getStatus() == BookingStatus.CANCELED) {
                    booking.setStatus(request.getStatus());
                } else {
                    throw new NoPermissionException("Недостаточно прав для данного запроса");
                }
            } else if (userId.equals(ownerId)) {
                switch (request.getStatus()) {
                    case APPROVED, REJECTED -> booking.setStatus(request.getStatus());
                    default -> throw new NoPermissionException("Недостаточно прав для данного запроса");
                }
            } else {
                throw new NoPermissionException("Недостаточно прав для данного запроса");
            }
        }
        booking = bookingRepository.save(bookingMapper.toBooking(request));
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public void delete(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + id + " не найден"));

        if (!booking.getBooker().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        bookingRepository.deleteById(id);
    }

    @Override
    public BookingResponse acceptBooking(Long id, Long userId, Boolean isAccept) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + id + " не найден"));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            booking.setStatus(isAccept ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            booking = bookingRepository.save(booking);
            return bookingMapper.toBookingResponse(booking);
        } else {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }
    }


//    private void checkTimeIntersection(BookingModify request) {
//        bookingRepository.findAll().stream()
//                .filter(booking -> booking.getItem().getId().equals(request.getItem().getId()))
//                .peek(booking -> {
//                    if (Intersection.timeIntersection(
//                            booking.getStart(),
//                            booking.getEnd(),
//                            request.getStart(),
//                            request.getEnd())
//                    ) {
//                        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
//                            throw new DateTimeAlreadyTakenException("Выбранное время уже забронировано");
//                        }
//                    }
//                })
//                .close();
//    }
}
