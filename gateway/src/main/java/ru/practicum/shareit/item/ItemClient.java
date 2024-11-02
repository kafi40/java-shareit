package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItem(long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemsForUser(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> patchItem(long itemId, long userId, ItemDto request) {
        return patch("/" + itemId, userId, request);
    }

    public ResponseEntity<Object> deleteItem(long itemId, long userId) {
        return patch("/" + itemId, userId);
    }

    public ResponseEntity<Object> getBySearch(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", parameters);
    }

    public ResponseEntity<Object> createComment(long itemId, long bookerId, CommentDto request) {
        return post("/" + itemId + "/comment", bookerId, request);
    }
}
