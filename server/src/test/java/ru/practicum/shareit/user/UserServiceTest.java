package ru.practicum.shareit.user;

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
import ru.practicum.shareit.exception.MailAlreadyUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.factory.ModelFactory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceTest {
    private final UserService userService;
    private final EntityManager em;
    private UserDto userDto;
    private UserResponse userResponse;
    private final Long notExistsUserId = 10000L;

    @BeforeEach
    void beforeEach() {
        userDto = ModelFactory.createUserDto();
        userResponse = userService.createUser(userDto);
    }


    @Test
    void testGetUser() {
        userResponse = userService.getUser(userResponse.id());
        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userResponse.id())
                .getSingleResult();
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userResponse.name()));
        assertThat(user.getEmail(), equalTo(userResponse.email()));
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(notExistsUserId));
    }

    @Test
    @Rollback
    void testCreateUser() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userResponse.id())
                        .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @Rollback
    void testPatchUser() {
        userDto = ModelFactory.createUserDto();
        userResponse = userService.patchUser(userResponse.id(), userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", userResponse.id())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(userResponse.id());

        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.id = :id", User.class);
        int count = query.setParameter("id", userResponse.id()).getFirstResult();

        assertThat(count, equalTo(0));
    }

    @Test
    void mustThrowExceptionTestCheckSuchUser() {
        Assertions.assertThrows(MailAlreadyUserException.class, () -> userService.createUser(userDto));
    }

    @Test
    void notThrowExceptionTestCheckSuchUser() {
        Assertions.assertDoesNotThrow(() -> userService.patchUser(userResponse.id(), userDto));
    }
}
