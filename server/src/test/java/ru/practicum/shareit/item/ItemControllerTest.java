package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    private final Long itemId = 1L;
    private final Long userId = 1L;
    private final ItemResponse itemResponse = ModelFactory.createItemResponse(itemId);
    private final CommentResponse commentResponse = ModelFactory.createCommentResponse(itemId);

    @Test
    void testGetItem() throws Exception {
        when(itemService.getItem(itemId)).thenReturn(itemResponse);
        mockMvc.perform(get("/items/" + itemId)
                        .content(mapper.writeValueAsString(itemResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.name()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponse.description()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponse.available()), Boolean.class))
                .andExpect(jsonPath("$.owner.id", is(itemResponse.owner().id()), Long.class));
        verify(itemService, times(1)).getItem(itemId);
    }

    @Test
    void testCreateItem() throws Exception {
        when(itemService.createItem(eq(userId), any(ItemDto.class))).thenReturn(itemResponse);
        mockMvc.perform(post("/items")
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.name()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponse.description()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponse.available()), Boolean.class))
                .andExpect(jsonPath("$.owner.id", is(itemResponse.owner().id()), Long.class));
        verify(itemService, times(1)).createItem(eq(userId), any(ItemDto.class));
    }

    @Test
    void testPatchItem() throws Exception {
        when(itemService.patchItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(itemResponse);
        mockMvc.perform(patch("/items/" + itemId)
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.name()), String.class))
                .andExpect(jsonPath("$.description", is(itemResponse.description()), String.class))
                .andExpect(jsonPath("$.available", is(itemResponse.available()), Boolean.class))
                .andExpect(jsonPath("owner").exists());
        verify(itemService, times(1)).patchItem(eq(userId), eq(itemId), any(ItemDto.class));
    }

    @Test
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/" + itemId)
                        .header("X-sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        verify(itemService, times(1)).deleteItem(eq(itemId), eq(userId));

    }

    @Test
    void testGetItemsForUser() throws Exception {
        when(itemService.getItemsForUser(userId)).thenReturn(List.of(itemResponse));
        mockMvc.perform(get("/items")
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
        verify(itemService, times(1)).getItemsForUser(userId);
    }

    @Test
    void testSearchItem() throws Exception {
        when(itemService.getBySearch(anyString())).thenReturn(List.of(itemResponse));
        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .content(mapper.writeValueAsString(itemResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
        verify(itemService, times(1)).getBySearch(anyString());
    }

    @Test
    void testCreateComment() throws Exception {
        when(itemService.createComment(eq(itemId), eq(userId), any(CommentDto.class))).thenReturn(commentResponse);
        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header("X-sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentResponse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponse.id()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponse.text()), String.class))
                .andExpect(jsonPath("$.created", is(commentResponse.created().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), LocalDateTime.class))
                .andExpect(jsonPath("item").exists())
                .andExpect(jsonPath("$.authorName", is(commentResponse.authorName()), String.class));
        verify(itemService, times(1)).createComment(eq(itemId), eq(userId), any(CommentDto.class));
    }
}