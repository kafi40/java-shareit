package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.base.BaseRepositoryImpl;
import ru.practicum.shareit.user.model.User;

@Repository
public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {
}
