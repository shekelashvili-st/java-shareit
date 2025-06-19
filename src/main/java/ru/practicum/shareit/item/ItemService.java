package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdMismatchException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    public ItemDto create(CreateItemDto item, Long userId) {
        validateUser(userId);
        Item newItem = mapper.createDtoToModel(item);
        newItem.setOwnerId(userId);
        Item itemFromDb = repository.save(newItem);
        return mapper.modelToDto(itemFromDb);
    }

    public ItemDto update(UpdateItemDto item, Long itemId, Long userId) {
        validateUser(userId);
        Item foundItem = repository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item with id " + itemId + " not found!"));
        validateItemOwner(foundItem, userId);
        if (item.getName() != null) {
            foundItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            foundItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            foundItem.setAvailable(item.getAvailable());
        }
        Item itemFromDb = repository.save(foundItem);
        return mapper.modelToDto(itemFromDb);
    }

    public ItemDto findById(Long itemId) {
        Item foundItem = repository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item with id " + itemId + " not found!"));
        return mapper.modelToDto(foundItem);
    }

    public Collection<ItemDto> findAllForUser(Long userId) {
        validateUser(userId);
        return repository.findAllForUser(userId).stream().map(mapper::modelToDto).toList();
    }

    public Collection<ItemDto> findByString(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return repository.findByString(text).stream().map(mapper::modelToDto).toList();
    }

    private void validateUser(Long userId) {
        if (userId == null) {
            throw new IdNotFoundException("User id is null!");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id" + userId + " not found!"));
    }

    private void validateItemOwner(Item item, Long userId) {
        if (!Objects.equals(item.getOwnerId(), userId)) {
            throw new IdMismatchException("Only the owner can update item information!");
        }
    }
}
