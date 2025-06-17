package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {
    private static long count = 0;
    private final Map<Long, User> users = new HashMap<>();

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++count);
        }
        users.put(count, user);
        return user;
    }

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(user -> Objects.equals(user.getEmail(), email)).findFirst();
    }

    public void deleteById(long id) {
        users.remove(id);
    }
}
