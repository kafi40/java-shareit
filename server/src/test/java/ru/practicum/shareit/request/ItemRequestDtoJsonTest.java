package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestResponse> jsonResponse;
    private final JacksonTester<ItemRequestDto> jsonDto;

    @Test
    void testItemRequestResponse() throws Exception {
        ItemRequestResponse itemRequestResponse = ModelFactory.createItemRequestResponse(1L);
        JsonContent<ItemRequestResponse> result = jsonResponse.write(itemRequestResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestResponse.id().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestResponse.description());
        assertThat(result).hasJsonPath("$.requestor");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestResponse.created().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = ModelFactory.createItemRequestDto();
        JsonContent<ItemRequestDto> result = jsonDto.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
