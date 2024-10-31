package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserResponse> jsonResponse;
    private final JacksonTester<UserDto> jsonDto;

    @Test
    void testUserResponse() throws Exception {
        UserResponse userResponse = ModelFactory.createUserResponse(1L);
        JsonContent<UserResponse> result = jsonResponse.write(userResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userResponse.id().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userResponse.name());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userResponse.email());
    }

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = ModelFactory.createUserDto();
        JsonContent<UserDto> result = jsonDto.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
