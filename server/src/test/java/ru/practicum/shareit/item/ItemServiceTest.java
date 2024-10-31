package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;

import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final EntityManager em;
    private UserResponse ownerResponse;
    private ItemResponse itemResponse;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        UserDto userDto = ModelFactory.createUserDto();
        ownerResponse = userService.createUser(userDto);

        itemDto = ModelFactory.createItemDto(ownerResponse.id());
        itemResponse = itemService.createItem(ownerResponse.id(), itemDto);
    }

    @Test
    void testGetItem() {
        itemResponse = itemService.getItem(itemResponse.id());

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemResponse.id())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemResponse.name()));
        assertThat(item.getDescription(), equalTo(itemResponse.description()));
        assertThat(item.getAvailable(), equalTo(itemResponse.available()));
        assertThat(item.getOwner().getId(), equalTo(itemResponse.owner().id()));

    }

    @Test
    @Rollback
    void testCreateItem() {
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemResponse.id())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getOwner().getId(), equalTo(itemDto.getOwnerId()));
    }

    @Test
    @Rollback
    void testPatchItem() {
        itemDto = ModelFactory.createItemDto(ownerResponse.id());
        itemResponse = itemService.patchItem(itemResponse.id(), ownerResponse.id(), itemDto);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", itemResponse.id())
                .getSingleResult();

        assertThat(item.getId(), equalTo(itemResponse.id()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    @Rollback
    void testDeleteItem() {
        itemService.deleteItem(itemResponse.id(), ownerResponse.id());
        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.id = :id", Item.class);
        int count = query.setParameter("id", itemResponse.id()).getFirstResult();

        assertThat(count, equalTo(0));
    }

    @Test
    @Rollback
    void testGetItemsForUser() {
        itemDto = ModelFactory.createItemDto(ownerResponse.id());
        itemResponse = itemService.createItem(ownerResponse.id(), itemDto);

        List<ItemResponse> itemResponses = itemService.getItemsForUser(ownerResponse.id());

        assertThat(itemResponses.size(), equalTo(2));

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE owner.id = :ownerId", Item.class);
        List<Item> items = query.setParameter("ownerId", ownerResponse.id()).getResultList();

        assertThat(itemResponses.size(), equalTo(items.size()));
    }

    @Test
    void testGetBySearch() {
        itemDto = ModelFactory.createItemDto(ownerResponse.id());
        itemDto.setName("otherName");
        itemResponse = itemService.createItem(ownerResponse.id(), itemDto);

        List<ItemResponse> itemResponses = itemService.getBySearch(itemDto.getDescription());

        assertThat(itemResponses.size(), equalTo(2));

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.name LIKE :name OR i.description LIKE :description", Item.class);
        List<Item> items = query
                .setParameter("name", itemDto.getDescription())
                .setParameter("description", itemDto.getDescription())
                .getResultList();

        assertThat(itemResponses.size(), equalTo(items.size()));
    }

    @Test
    @Rollback
    void testCreateComment() {
        UserDto bookerDto = ModelFactory.createUserDto();
        UserResponse bookerResponse = userService.createUser(bookerDto);

        BookingDto bookingDto = ModelFactory.createBookingDto(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                itemResponse.id(),
                BookingStatus.WAITING
        );
        BookingResponse bookingResponse = bookingService.createBooking(bookerResponse.id(), bookingDto);

        bookingService.acceptBooking(bookingResponse.id(), ownerResponse.id(), true);

        CommentDto commentRequest = new CommentDto();
        commentRequest.setText("testCreateComment");
        CommentResponse commentResponse = itemService.createComment(itemResponse.id(), bookerResponse.id(), commentRequest);

        TypedQuery<Comment> query = em.createQuery("SELECT c FROM Comment AS c WHERE c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", commentResponse.id())
                .getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(commentRequest.getText()));
        assertThat(comment.getItem().getId(), equalTo(itemResponse.id()));
        assertThat(comment.getAuthor().getId(), equalTo(bookerResponse.id()));
    }
}
