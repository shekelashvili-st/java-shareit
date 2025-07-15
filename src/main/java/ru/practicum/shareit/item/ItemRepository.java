package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByAvailableIsTrueAndNameContainingOrDescriptionContainingAllIgnoreCase(String text, String text2);

    List<Item> findAllByOwnerId(Long id);
}
