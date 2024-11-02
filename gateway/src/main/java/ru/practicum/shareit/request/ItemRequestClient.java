package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getItemRequest(long bookingId) {
        return get("/" + bookingId);
    }

    public ResponseEntity<Object> getItemRequestForRequestor(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> createItemRequest(long userId, ItemRequestDto request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> patchItemRequest(long userId, ItemRequestDto request) {
        return patch("", userId, request);
    }

    public ResponseEntity<Object> deleteItemRequest(long userId, long itemRequestId) {
        return delete("/" + itemRequestId, userId);
    }

    public ResponseEntity<Object> getItemRequestForOther(long userId) {
        return get("/all", userId);
    }
}
