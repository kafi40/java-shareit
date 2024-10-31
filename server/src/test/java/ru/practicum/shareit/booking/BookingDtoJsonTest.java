package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.factory.ModelFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingResponse> jsonResponse;
    private final JacksonTester<BookingDto> jsonDto;

    @Test
    void testBookingResponse() throws Exception {
        BookingResponse bookingResponse = ModelFactory.createBookingResponse(1L);
        JsonContent<BookingResponse> result = jsonResponse.write(bookingResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingResponse.id().intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(bookingResponse.start().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingResponse.end().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).hasJsonPathValue("$.item");
        assertThat(result).hasJsonPathValue("$.booker");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingResponse.status().toString());
        assertThat(result).extractingJsonPathStringValue("$.state").isEqualTo(bookingResponse.state().toString());
    }

    @Test
    void testBookingDto() throws Exception {
        BookingDto bookingDto = ModelFactory.createBookingDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L, BookingStatus.WAITING);
        JsonContent<BookingDto> result = jsonDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(bookingDto.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingDto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}
