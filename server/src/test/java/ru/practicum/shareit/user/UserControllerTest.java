package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    private final Long userId = 1L;
    private final UserResponse userResponse = ModelFactory.createUserResponse(userId);

    @Test
    void testGetUser() throws Exception {
        when(userService.getUser(anyLong())).thenReturn(userResponse);

        mockMvc.perform(get("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.name()), String.class))
                .andExpect(jsonPath("$.email", is(userResponse.email()), String.class));

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.name()), String.class))
                .andExpect(jsonPath("$.email", is(userResponse.email()), String.class));
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void testPatchUser() throws Exception {
        when(userService.patchUser(eq(userId), any(UserDto.class))).thenReturn(userResponse);

        mockMvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(userResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponse.name()), String.class))
                .andExpect(jsonPath("$.email", is(userResponse.email()), String.class));
        verify(userService, times(1)).patchUser(eq(userId), any(UserDto.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(userService, times(1)).deleteUser(userId);
    }
}
