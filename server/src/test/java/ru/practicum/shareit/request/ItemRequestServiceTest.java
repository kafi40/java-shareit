package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager em;
    private UserResponse requestorResponse;
    private ItemRequestResponse  itemRequestResponse;
    private ItemRequestDto itemRequestDto;
    private final Long notExistsItemRequestId = 10000L;

    @BeforeEach
    void beforeEach() {
        UserDto userDto = ModelFactory.createUserDto();
        requestorResponse = userService.createUser(userDto);
        itemRequestDto = ModelFactory.createItemRequestDto();
        itemRequestResponse = itemRequestService.createItemRequest(requestorResponse.id(), itemRequestDto);
    }

    @Test
    void testGetItemRequest() {
        ItemRequestWithItems itemRequestWithItems = itemRequestService.getItemRequest(itemRequestResponse.id());
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest AS i WHERE i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestWithItems.id())
                .getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestResponse.description()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(itemRequestResponse.requestor().id()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestResponse.created()));
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(notExistsItemRequestId));
    }

    @Test
    @Rollback
    void testCreateItemRequest() {
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest AS i WHERE i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestResponse.id())
                .getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getCreated(), equalTo(itemRequestDto.getCreated()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(requestorResponse.id()));
    }

    @Test
    @Rollback
    void testPatchItemRequest() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("newDescription");
        itemRequestResponse = itemRequestService.patchItemRequest(itemRequestResponse.id(), requestorResponse.id(), itemRequestDto);
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest AS i WHERE i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestResponse.id())
                .getSingleResult();
        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(requestorResponse.id()));
    }

    @Test
    @Rollback
    void testDeleteItemRequest() {
        itemRequestService.deleteItemRequest(requestorResponse.id(), itemRequestResponse.id());
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest AS i WHERE i.id = :id", ItemRequest.class);
        int count = query.setParameter("id", itemRequestResponse.id()).getFirstResult();
        assertThat(count, equalTo(0));
    }

    @Test
    @Rollback
    void testGetItemRequestForRequestor() {
        itemRequestResponse = itemRequestService.createItemRequest(requestorResponse.id(), itemRequestDto);
        UserDto owner = ModelFactory.createUserDto();
        UserResponse ownerResponse = userService.createUser(owner);
        ItemDto itemDto = ModelFactory.createItemDtoForRequest(ownerResponse.id(), itemRequestResponse.id());
        itemService.createItem(ownerResponse.id(), itemDto);
        List<ItemRequestWithItems> requestWithItems = itemRequestService.getItemRequestForRequestor(requestorResponse.id());
        assertThat(requestWithItems.size(), equalTo(2));
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest AS i WHERE i.requestor.id = :requestor_id", ItemRequest.class);
        List<ItemRequest> itemRequests = query.setParameter("requestor_id", requestorResponse.id())
                .getResultList();
        assertThat(requestWithItems.size(), equalTo(itemRequests.size()));
    }

    @Test
    void testGetItemRequestForOther() {
        itemRequestResponse = itemRequestService.createItemRequest(requestorResponse.id(), itemRequestDto);
        UserDto owner = ModelFactory.createUserDto();
        UserResponse ownerResponse = userService.createUser(owner);
        ItemDto itemDto = ModelFactory.createItemDtoForRequest(ownerResponse.id(), itemRequestResponse.id());
        itemService.createItem(ownerResponse.id(), itemDto);
        List<ItemRequestResponse> requestWithItems = itemRequestService.getItemRequestForOther(requestorResponse.id());
        assertThat(requestWithItems.size(), equalTo(0));
        TypedQuery<ItemRequest> query = em.createQuery("SELECT i FROM ItemRequest AS i WHERE i.requestor.id <> :requestor_id", ItemRequest.class);
        List<ItemRequest> itemRequests = query.setParameter("requestor_id", requestorResponse.id())
                .getResultList();
        assertThat(requestWithItems.size(), equalTo(itemRequests.size()));
    }

    @Test
    void mustThrowExceptionTestPatchItemRequest() {
        UserDto userDto = ModelFactory.createUserDto();
        UserResponse newUser = userService.createUser(userDto);
        Assertions.assertThrows(NoPermissionException.class,
                () -> itemRequestService.patchItemRequest(itemRequestResponse.id(), newUser.id(), itemRequestDto));
    }

    @Test
    void mustThrowExceptionTestDeleteItemRequest() {
        UserDto userDto = ModelFactory.createUserDto();
        UserResponse newUser = userService.createUser(userDto);
        Assertions.assertThrows(NoPermissionException.class,
                () -> itemRequestService.deleteItemRequest(newUser.id(), itemRequestResponse.id()));
    }
}
