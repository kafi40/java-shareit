package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
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
    void testDeleteBooking() {
        bookingService.deleteBooking(bookingResponse.id(), bookerResponse.id());

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        int count = query.setParameter("id", bookingResponse.id()).getFirstResult();

        assertThat(count, equalTo(0));
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
    void testGetBookings() {
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
    void testGetBookingsForOwner() {
        UserDto newBooker = ModelFactory.createUserDto();
        UserResponse newOwnerResponse = userService.createUser(newBooker);

        bookingDto = ModelFactory.createBookingDto(start, end, itemResponse.id(), BookingStatus.WAITING);
        bookingService.createBooking(newOwnerResponse.id(), bookingDto);

        List<BookingResponse> bookingResponses = bookingService.getBookingsForOwner(ownerResponse.id(), BookingState.ALL);

        assertThat(bookingResponses.size(), equalTo(2));
    }
}
