package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.factory.ModelFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private final BookingResponse bookingResponse = ModelFactory.createBookingResponse(bookingId);

    @Test
    void testGetBookingForUser() throws Exception {
        when(bookingService.getBookingForUser(bookingId, userId)).thenReturn(bookingResponse);
        mockMvc.perform(get("/bookings/" + bookingId)
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.id()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.start().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingResponse.end().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("item").exists())
                .andExpect(jsonPath("booker").exists())
                .andExpect(jsonPath("$.status", is(bookingResponse.status().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.state", is(bookingResponse.state().toString()), BookingState.class));
        verify(bookingService, times(1)).getBookingForUser(eq(bookingId), eq(userId));
    }

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(eq(userId), any(BookingDto.class))).thenReturn(bookingResponse);
        mockMvc.perform(post("/bookings")
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.id()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.start().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingResponse.end().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("item").exists())
                .andExpect(jsonPath("booker").exists())
                .andExpect(jsonPath("$.status", is(bookingResponse.status().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.state", is(bookingResponse.state().toString()), BookingState.class));
        verify(bookingService, times(1)).createBooking(eq(userId), any(BookingDto.class));
    }

    @Test
    void testPatchBooking() throws Exception {
        when(bookingService.patchBooking(eq(bookingId), eq(userId), any(BookingDto.class))).thenReturn(bookingResponse);
        mockMvc.perform(patch("/bookings/" + bookingId)
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.id()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponse.start().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("$.end", is(bookingResponse.end().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("item").exists())
                .andExpect(jsonPath("booker").exists())
                .andExpect(jsonPath("$.status", is(bookingResponse.status().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.state", is(bookingResponse.state().toString()), BookingState.class));
        verify(bookingService, times(1)).patchBooking(eq(bookingId), eq(userId), any(BookingDto.class));
    }

    @Test
    void testPatchBookingForAccept() throws Exception {
        when(bookingService.patchBooking(eq(bookingId), eq(userId), any(BookingDto.class))).thenReturn(bookingResponse);
        mockMvc.perform(patch("/bookings/" + bookingId)
                        .header("X-sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());
        verify(bookingService, times(1)).acceptBooking(eq(bookingId), eq(userId), eq(true));
    }

    @Test
    void testDeleteBooking() throws Exception {
        mockMvc.perform(delete("/bookings/" + bookingId)
                        .header("X-sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(bookingService, times(1)).deleteBooking(eq(bookingId), eq(userId));
    }

    @Test
    void testGetBookingsForOwner() throws Exception {
        when(bookingService.getBookingsForOwner(eq(userId), eq(BookingState.ALL))).thenReturn(List.of(bookingResponse));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-sharer-User-Id", userId)
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(bookingResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
        verify(bookingService, times(1)).getBookingsForOwner(eq(userId), eq(BookingState.ALL));
    }

    @Test
    void testGetBookings() throws Exception {
        when(bookingService.getBookings(eq(userId), eq(BookingState.ALL), eq(0), eq(10))).thenReturn(List.of(bookingResponse));
        mockMvc.perform(get("/bookings")
                        .header("X-sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(bookingResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
        verify(bookingService, times(1)).getBookings(eq(userId), eq(BookingState.ALL), eq(0), eq(10));
    }
}
