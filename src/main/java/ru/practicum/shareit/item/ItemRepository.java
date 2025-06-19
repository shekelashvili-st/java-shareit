package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepository {
    private static long count = 0;
    private final Map<Long, Item> items = new HashMap<>();

    public Collection<Item> findAllForUser(long userId) {
        return items.values().stream().filter(item -> item.getOwnerId() == userId).toList();
    }

    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(++count);
        }
        items.put(item.getId(), item);
        return item;
    }
}
