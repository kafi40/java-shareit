package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingModify;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Intersection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse getForUser(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + id + " не найден"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toBookingResponse(booking);
        } else {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }
    }

    @Override
    public BookingResponse create(Long userId, BookingModify request) {
        checkTimeIntersection(request);
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
        if (request.getStart() != null || request.getEnd() != null) {
            checkTimeIntersection(request);
        }

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
            System.out.println(booking.getStatus());
            booking = bookingRepository.save(booking);
            return bookingMapper.toBookingResponse(booking);
        } else {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }
    }

    @Override
    public List<BookingResponse> getAllForBooker(Long bookerId, BookingState state) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + bookerId + " не найден"));
        List<Booking> bookings = state.equals(BookingState.ALL) ?
                bookingRepository.findAllByBooker_Id(bookerId) :
                bookingRepository.findAllByBooker_Id(bookerId).stream()
                        .filter(booking -> booking.getBookingState().equals(state))
                        .collect(Collectors.toList());
        return bookingMapper.toBookingResponseList(bookings);
    }

    @Override
    public List<BookingResponse> getAllForOwner(Long ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + ownerId + " не найден"));
        List<Long> itemsId = itemRepository.findAllByOwnerId(ownerId).stream()
                .map(Item::getId)
                .toList();
        if (itemsId.isEmpty()) {
            return new ArrayList<>();
        }
        List<Booking> bookings = state.equals(BookingState.ALL) ?
                bookingRepository.findAllByItem_IdIn(itemsId) :
                bookingRepository.findAllByItem_IdIn(itemsId).stream()
                        .filter(booking -> booking.getBookingState().equals(state))
                        .collect(Collectors.toList());
        return bookingMapper.toBookingResponseList(bookings);
    }


    private void checkTimeIntersection(BookingModify request) {
        bookingRepository.findAllByItem_Id(request.getItemId()).stream()
                .peek(booking -> {
                    if (Intersection.timeIntersection(
                            booking.getStart(),
                            booking.getEnd(),
                            request.getStart(),
                            request.getEnd())
                    ) {
                        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                            throw new DateTimeAlreadyTakenException("Выбранное время уже забронировано");
                        }
                    }
                })
                .close();
    }
}
