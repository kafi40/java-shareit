package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemResponse> jsonResponse;
    private final JacksonTester<ItemDto> jsonDto;

    @Test
    void testItemResponse() throws Exception {
        ItemResponse itemResponse = ModelFactory.createItemResponse(1L);
        JsonContent<ItemResponse> result = jsonResponse.write(itemResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemResponse.id().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemResponse.name());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemResponse.description());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemResponse.available());
        assertThat(result).hasJsonPathValue("$.owner");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).hasJsonPath("$.itemRequest");
        assertThat(result).hasJsonPath("$.rentCount");
    }

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = ModelFactory.createItemDto(1L);
        JsonContent<ItemDto> result = jsonDto.write(itemDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(itemDto.getOwnerId().intValue());
        assertThat(result).hasJsonPath("$.requestId");
    }
}
