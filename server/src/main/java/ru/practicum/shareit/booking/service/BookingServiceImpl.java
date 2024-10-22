package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Intersection;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse getBookingForUser(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + bookingId + " не найден"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return bookingMapper.toBookingResponse(booking);
        } else {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }
    }

    @Override
    public BookingResponse createBooking(long userId, BookingDto request) {
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
    public BookingResponse patchBooking(long bookingId, long userId, BookingDto request) {
        if (request.getStart() != null || request.getEnd() != null) {
            checkTimeIntersection(request);
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID = " + bookingId + " не найдено"));

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();
        if (request.getStart() != null && userId == bookingId) {
            booking.setStart(request.getStart());
        }
        if (request.getEnd() != null && userId == bookingId) {
            booking.setEnd(request.getEnd());
        }
        if (request.getStatus() != null) {
            if (userId == bookerId) {
                if (request.getStatus() == BookingStatus.CANCELED) {
                    booking.setStatus(request.getStatus());
                } else {
                    throw new NoPermissionException("Недостаточно прав для данного запроса");
                }
            } else if (userId == ownerId) {
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
    public void deleteBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + bookingId + " не найден"));

        if (!booking.getBooker().getId().equals(userId)) {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }

        bookingRepository.deleteById(bookingId);
    }

    @Override
    public BookingResponse acceptBooking(long bookingId, long userId, boolean isAccept) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование> с ID = " + bookingId + " не найден"));
        if (booking.getItem().getOwner().getId().equals(userId)) {
            booking.setStatus(isAccept ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            booking = bookingRepository.save(booking);
            return bookingMapper.toBookingResponse(booking);
        } else {
            throw new NoPermissionException("Недостаточно прав для данного запроса");
        }
    }

    @Override
    public List<BookingResponse> getBookings(long bookerId, BookingState state, long from, long size) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + bookerId + " не найден"));

        return state.equals(BookingState.ALL) ?
                bookingMapper.toBookingResponseList(bookingRepository.findAllByBooker_Id(bookerId)) :
                bookingRepository.findAllByBooker_Id(bookerId).stream()
                        .map(bookingMapper::toBookingResponse)
                        .filter(bookingResponse -> bookingResponse.state().equals(state))
                        .toList();
    }

    @Override
    public List<BookingResponse> getBookingsForOwner(long ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + ownerId + " не найден"));
        List<Long> itemsId = itemRepository.findAllByOwnerId(ownerId).stream()
                .map(Item::getId)
                .toList();
        if (itemsId.isEmpty()) {
            return new ArrayList<>();
        }

        return state.equals(BookingState.ALL) ?
                bookingMapper.toBookingResponseList(bookingRepository.findAllByItem_IdIn(itemsId)) :
                bookingRepository.findAllByItem_IdIn(itemsId).stream()
                        .map(bookingMapper::toBookingResponse)
                        .filter(bookingResponse -> bookingResponse.state().equals(state))
                        .toList();
    }


    private void checkTimeIntersection(BookingDto request) {
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
