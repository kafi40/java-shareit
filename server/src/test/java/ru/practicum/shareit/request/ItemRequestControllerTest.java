package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    private final Long itemRequestId = 1L;
    private final Long userId = 1L;
    private final ItemRequestResponse itemRequestResponse = ModelFactory.createItemRequestResponse(itemRequestId);
    private final ItemRequestWithItems itemRequestWithItems = ModelFactory.createItemRequestWithItems(itemRequestId);

    @Test
    void testGetItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(itemRequestId)).thenReturn(itemRequestWithItems);

        mockMvc.perform(get("/requests/" + itemRequestId)
                        .content(mapper.writeValueAsString(itemRequestResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponse.id()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponse.description()), String.class))
                .andExpect(jsonPath("requestor").exists())
                .andExpect(jsonPath("$.created", is(itemRequestResponse.created().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("requestor").exists());

        verify(itemRequestService, times(1)).getItemRequest(itemRequestId);
    }

    @Test
    void testGetItemRequestForRequestor() throws Exception {
        when(itemRequestService.getItemRequestForRequestor(itemRequestId)).thenReturn(List.of(itemRequestWithItems));

        mockMvc.perform(get("/requests")
                        .header("X-sharer-User-Id", userId)
                        .param("state", "ALL")
                        .content(mapper.writeValueAsString(itemRequestResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

        verify(itemRequestService, times(1)).getItemRequestForRequestor(userId);
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(eq(userId), any(ItemRequestDto.class))).thenReturn(itemRequestResponse);

        mockMvc.perform(post("/requests")
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponse.id()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponse.description()), String.class))
                .andExpect(jsonPath("requestor").exists())
                .andExpect(jsonPath("$.created", is(itemRequestResponse.created().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class));

        verify(itemRequestService, times(1)).createItemRequest(eq(itemRequestId), any(ItemRequestDto.class));
    }

    @Test
    void testPatchItemRequest() throws Exception {
        when(itemRequestService.patchItemRequest(eq(itemRequestId), eq(userId), any(ItemRequestDto.class))).thenReturn(itemRequestResponse);

        mockMvc.perform(patch("/requests/" + itemRequestId)
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponse.id()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponse.description()), String.class))
                .andExpect(jsonPath("requestor").exists())
                .andExpect(jsonPath("$.created", is(itemRequestResponse.created().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class));

        verify(itemRequestService, times(1)).patchItemRequest(eq(itemRequestId), eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void testDeleteItemRequest() throws Exception {
        mockMvc.perform(delete("/requests/" + itemRequestId)
                        .header("X-sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(itemRequestService, times(1)).deleteItemRequest(eq(itemRequestId), eq(userId));
    }

    @Test
    void testGetItemRequestForOther() throws Exception {
        when(itemRequestService.getItemRequestForOther(userId)).thenReturn(List.of(itemRequestResponse));

        mockMvc.perform(get("/requests/all")
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));

        verify(itemRequestService, times(1)).getItemRequestForOther(userId);
    }
}
