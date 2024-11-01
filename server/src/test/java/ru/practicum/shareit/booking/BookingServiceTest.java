package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.DateTimeAlreadyTakenException;
import ru.practicum.shareit.exception.ItemIsUnavailableException;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager em;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(2);
    private UserResponse ownerResponse;
    private UserResponse bookerResponse;
    private ItemResponse itemResponse;
    private BookingResponse bookingResponse;
    private BookingDto bookingDto;
    private final Long notExistsBookingId = 10000L;

    @BeforeEach
    void beforeEach() {
        UserDto userDto = ModelFactory.createUserDto();
        ownerResponse = userService.createUser(userDto);
        ItemDto itemDto = ModelFactory.createItemDto(ownerResponse.id());
        itemResponse = itemService.createItem(ownerResponse.id(), itemDto);
        userDto = ModelFactory.createUserDto();
        bookerResponse = userService.createUser(userDto);
        bookingDto = ModelFactory.createBookingDto(start, end, itemResponse.id(), BookingStatus.WAITING);
        bookingResponse = bookingService.createBooking(bookerResponse.id(), bookingDto);
    }

    @Test
    @Rollback
    void testGetBookingForUser()  {
        bookingResponse = bookingService.getBookingForUser(bookingResponse.id(), bookerResponse.id());
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingResponse.id())
                .getSingleResult();
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingResponse.start()));
        assertThat(booking.getEnd(), equalTo(bookingResponse.end()));
        assertThat(booking.getStatus(), equalTo(bookingResponse.status()));
        assertThat(booking.getItem().getId(), equalTo(bookingResponse.item().id()));
        assertThat(booking.getBooker().getId(), equalTo(bookingResponse.booker().id()));
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingForUser(bookerResponse.id(),notExistsBookingId));
    }

    @Test
    @Rollback
    void testCreateBooking() {
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingResponse.id())
                .getSingleResult();
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(booking.getStatus(), equalTo(bookingDto.getStatus()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getBooker().getId(), equalTo(bookerResponse.id()));
    }

    @Test
    @Rollback
    void testPatchBooking() {
        bookingDto = ModelFactory.createBookingDto(start, end, null, null);
        bookingResponse = bookingService.patchBooking(bookingResponse.id(), bookerResponse.id(), bookingDto);
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingResponse.id())
                .getSingleResult();
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDto.getEnd()));
    }

    @Test
    @Rollback
    void testDeleteBooking() {
        bookingService.deleteBooking(bookingResponse.id(), bookerResponse.id());
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        int count = query.setParameter("id", bookingResponse.id()).getFirstResult();
        assertThat(count, equalTo(0));
    }

    @Test
    @Rollback
    void mustThrowExceptionForBookingUsedDateTime() {
        bookingResponse = bookingService.acceptBooking(bookingResponse.id(), ownerResponse.id(), true);
        UserDto userDto = ModelFactory.createUserDto();
        bookerResponse = userService.createUser(userDto);
        bookingDto = ModelFactory.createBookingDto(start, end, itemResponse.id(), BookingStatus.WAITING);
        Assertions.assertThrows(DateTimeAlreadyTakenException.class,
                () -> bookingService.createBooking(bookerResponse.id(), bookingDto));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestCreateBookingForUnavailableItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(false);
        itemService.patchItem(itemResponse.id(), ownerResponse.id(), itemDto);
        bookingDto = ModelFactory.createBookingDto(start, end, itemResponse.id(), BookingStatus.WAITING);
        Assertions.assertThrows(ItemIsUnavailableException.class,
                () -> bookingService.createBooking(bookerResponse.id(), bookingDto));
    }

    @Test
    @Rollback
    void testAcceptBooking() {
        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingResponse.id())
                .getSingleResult();
        bookingResponse = bookingService.acceptBooking(bookingResponse.id(), ownerResponse.id(), true);
        assertThat(booking.getId(), equalTo(bookingResponse.id()));
        assertThat(booking.getStart(), equalTo(bookingResponse.start()));
        assertThat(booking.getEnd(), equalTo(bookingResponse.end()));
        assertThat(booking.getItem().getId(), equalTo(bookingResponse.item().id()));
        assertThat(booking.getBooker().getId(), equalTo(bookingResponse.booker().id()));
        assertThat(BookingStatus.APPROVED, equalTo(bookingResponse.status()));
    }

    @Test
    @Rollback
    void testGetBookingsWithStateAll() {
        UserDto newOwner = ModelFactory.createUserDto();
        UserResponse newOwnerResponse = userService.createUser(newOwner);
        ItemDto newItemDto = ModelFactory.createItemDto(newOwnerResponse.id());
        ItemResponse newItemResponse = itemService.createItem(newOwnerResponse.id(), newItemDto);
        bookingDto = ModelFactory.createBookingDto(start, end, newItemResponse.id(), BookingStatus.WAITING);
        bookingService.createBooking(bookerResponse.id(), bookingDto);
        List<BookingResponse> bookingResponses = bookingService.getBookings(bookerResponse.id(), BookingState.ALL, 1, 10);
        assertThat(bookingResponses.size(), equalTo(2));
    }

    @Test
    @Rollback
    void testGetBookingsWithStatePast() {
        List<BookingResponse> bookingResponses = bookingService.getBookings(bookerResponse.id(), BookingState.PAST, 1, 10);
        assertThat(bookingResponses.size(), equalTo(0));
    }

    @Test
    @Rollback
    void testGetBookingsForOwnerWithStateAll() {
        UserDto newBooker = ModelFactory.createUserDto();
        UserResponse newOwnerResponse = userService.createUser(newBooker);
        bookingDto = ModelFactory.createBookingDto(start, end, itemResponse.id(), BookingStatus.WAITING);
        bookingService.createBooking(newOwnerResponse.id(), bookingDto);
        List<BookingResponse> bookingResponses = bookingService.getBookingsForOwner(ownerResponse.id(), BookingState.ALL);
        assertThat(bookingResponses.size(), equalTo(2));
    }

    @Test
    @Rollback
    void testGetBookingsForOwnerWithStatePast() {
        UserDto newBooker = ModelFactory.createUserDto();
        UserResponse newOwnerResponse = userService.createUser(newBooker);
        bookingDto = ModelFactory.createBookingDto(start, end, itemResponse.id(), BookingStatus.WAITING);
        bookingService.createBooking(newOwnerResponse.id(), bookingDto);
        List<BookingResponse> bookingResponses = bookingService.getBookingsForOwner(ownerResponse.id(), BookingState.PAST);
        assertThat(bookingResponses.size(), equalTo(0));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestGetBookingsForOwnerWithoutItem() {
        UserDto newOwner = ModelFactory.createUserDto();
        UserResponse newOwnerResponse = userService.createUser(newOwner);
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsForOwner(newOwnerResponse.id(), BookingState.ALL));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestGetBookingForUser() {
        UserDto userDto = ModelFactory.createUserDto();
        UserResponse newUser = userService.createUser(userDto);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.getBookingForUser(bookingResponse.id(), newUser.id()));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestPatchBooking() {
        UserDto userDto = ModelFactory.createUserDto();
        UserResponse newUser = userService.createUser(userDto);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.patchBooking(bookingResponse.id(), newUser.id(), bookingDto));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestAcceptBooking() {
        UserDto userDto = ModelFactory.createUserDto();
        UserResponse newUser = userService.createUser(userDto);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.acceptBooking(bookingResponse.id(), newUser.id(), true));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestDeleteBooking() {
        UserDto userDto = ModelFactory.createUserDto();
        UserResponse newUser = userService.createUser(userDto);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.deleteBooking(bookingResponse.id(), newUser.id()));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestPatchOwnerTryCancelBooking() {
        BookingDto cancelBooking = new BookingDto();
        cancelBooking.setStatus(BookingStatus.CANCELED);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.patchBooking(bookingResponse.id(), ownerResponse.id(), cancelBooking));
    }

    @Test
    @Rollback
    void notThrowExceptionTestPatchOwnerTryApprovedBooking() {
        BookingDto approvedBooking = new BookingDto();
        approvedBooking.setStatus(BookingStatus.APPROVED);
        Assertions.assertDoesNotThrow(() -> bookingService.patchBooking(bookingResponse.id(), ownerResponse.id(), approvedBooking));
    }

    @Test
    @Rollback
    void notThrowExceptionTestPatchOwnerTryRejectedBooking() {
        BookingDto rejectedBooking = new BookingDto();
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        Assertions.assertDoesNotThrow(() -> bookingService.patchBooking(bookingResponse.id(), ownerResponse.id(), rejectedBooking));
    }

    @Test
    @Rollback
    void notThrowExceptionTestPatchOwnerTryChangeDateTime() {
        BookingDto changeStart = new BookingDto();
        changeStart.setStart(start);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.patchBooking(bookingResponse.id(), ownerResponse.id(), changeStart));
        BookingDto changeEnd = new BookingDto();
        changeEnd.setEnd(end);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.patchBooking(bookingResponse.id(), ownerResponse.id(), changeEnd));
    }

    @Test
    @Rollback
    void notThrowExceptionTestPatchBookerTryCancelBooking() {
        BookingDto cancelBooking = new BookingDto();
        cancelBooking.setStatus(BookingStatus.CANCELED);
        Assertions.assertDoesNotThrow(() -> bookingService.patchBooking(bookingResponse.id(), bookerResponse.id(), cancelBooking));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestPatchBookerTryApprovedBooking() {
        BookingDto approvedBooking = new BookingDto();
        approvedBooking.setStatus(BookingStatus.APPROVED);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.patchBooking(bookingResponse.id(), bookerResponse.id(), approvedBooking));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestPatchBookerTryRejectedBooking() {
        BookingDto rejectedBooking = new BookingDto();
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.patchBooking(bookingResponse.id(), bookerResponse.id(), rejectedBooking));
    }

    @Test
    @Rollback
    void mustThrowExceptionTestPatchForOtherUser() {
        UserDto otherUser = ModelFactory.createUserDto();
        UserResponse otherUserResponse = userService.createUser(otherUser);
        Assertions.assertThrows(NoPermissionException.class,
                () -> bookingService.patchBooking(bookingResponse.id(), otherUserResponse.id(), bookingDto));
    }
}
