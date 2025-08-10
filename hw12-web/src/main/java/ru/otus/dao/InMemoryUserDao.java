package ru.otus.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ru.otus.model.User;

@SuppressWarnings("java:S2068")
public class InMemoryUserDao implements UserDao {

    private final Map<Long, User> users;

    public InMemoryUserDao() {
        users = new HashMap<>();
        users.put(1L, new User(1L, "Name1", "admin1", "1111"));
        users.put(2L, new User(2L, "Name2", "admin2", "2222"));
        users.put(3L, new User(3L, "Name3", "admin3", "3333"));
        users.put(4L, new User(4L, "Name4", "admin4", "4444"));
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream().filter(v -> v.getLogin().equals(login)).findFirst();
    }
}
