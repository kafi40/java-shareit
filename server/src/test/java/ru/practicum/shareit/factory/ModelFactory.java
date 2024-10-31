package ru.practicum.shareit.factory;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ModelFactory {
    public static UserResponse createUserResponse(Long id) {
        return new UserResponse(id, "Test", "test@test.com");
    }

    public static ItemResponse createItemResponse(Long id) {
        return new ItemResponse(
                id,
                "Test",
                "test",
                true,
                createUserResponse(1L),
                null,
                null,
                null,
                null,
                0
        );
    }

    public static CommentResponse createCommentResponse(Long id) {
        return new CommentResponse(
                1L,
                "Test",
                LocalDateTime.of(2024, 12, 12, 1, 0),
                createItemResponse(id),
                "Test"
        );
    }

    public static BookingResponse createBookingResponse(Long id) {
        return new BookingResponse(
                id,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createItemResponse(1L),
                createUserResponse(1L),
                BookingStatus.WAITING,
                BookingState.WAITING
        );
    }

    public static ItemRequestResponse createItemRequestResponse(Long id) {
        return new ItemRequestResponse(
                id,
                "test",
                createUserResponse(1L),
                LocalDateTime.now()
        );
    }

    public static ItemRequestWithItems createItemRequestWithItems(Long id) {
        return new ItemRequestWithItems(
                id,
                "test",
                createUserResponse(1L),
                LocalDateTime.now(),
                new ArrayList<>()
        );
    }

    public static BookingDto createBookingDto(LocalDateTime start, LocalDateTime end, Long itemId, BookingStatus status) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);
        bookingDto.setStatus(status);
        return bookingDto;
    }


    public static UserDto createUserDto() {
        UserDto userDto = new UserDto();
        userDto.setName("Test");
        userDto.setEmail("test" + Math.random() * 1000 + "@test.com");
        return userDto;
    }

    public static ItemDto createItemDto(Long ownerId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("testName");
        itemDto.setDescription("testDescription");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(ownerId);
        return itemDto;
    }

    public static ItemDto createItemDtoForRequest(Long ownerId, Long requestId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("testName");
        itemDto.setDescription("testDescription");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(ownerId);
        itemDto.setRequestId(requestId);
        return itemDto;
    }

    public static ItemRequestDto createItemRequestDto() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("test");
        itemRequestDto.setCreated(LocalDateTime.now());
        return itemRequestDto;
    }
}
