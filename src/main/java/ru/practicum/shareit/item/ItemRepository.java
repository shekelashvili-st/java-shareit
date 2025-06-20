package ru.practicum.shareit.item;

import org.apache.commons.lang3.StringUtils;
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

    public Collection<Item> findByString(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> StringUtils.containsIgnoreCase(item.getName(), text)
                        || StringUtils.containsIgnoreCase(item.getDescription(), text))
                .toList();
    }

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(++count);
        }
        items.put(item.getId(), item);
        return item;
    }
}
