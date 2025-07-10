package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.IdMismatchException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;

    public ItemDto create(CreateItemDto item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id" + userId + " not found!"));
        Item newItem = mapper.createDtoToModel(item);
        newItem.setOwner(user);
        Item itemFromDb = repository.save(newItem);
        return mapper.modelToDto(itemFromDb);
    }

    public ItemDto update(UpdateItemDto item, Long itemId, Long userId) {
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

    public Collection<ItemWithBookingsDto> findAllForUser(Long userId) {
        validateUser(userId);
        List<Item> items = repository.findAllByOwnerId(userId);
        List<ItemWithBookingsDto> dtos = new ArrayList<>();
        for (Item item : items) {
            ItemWithBookingsDto dto = mapper.modelToWithBookingsDto(item);
            List<Timestamp> startTimes = bookingRepository.findAllByItemIdOrderByStart(dto.getId())
                    .stream().map(Booking::getStart).toList();
            if (!startTimes.isEmpty()) {
                int index = Collections.binarySearch(startTimes, Timestamp.from(Instant.now()));
                if (index < 0) {
                    index = -index - 1;
                }
                if (index == 0) {
                    dto.setStartNextBooking(startTimes.get(index));
                } else if (index == startTimes.size()) {
                    dto.setStartPreviousBooking(startTimes.get(index - 1));
                } else {
                    dto.setStartPreviousBooking(startTimes.get(index - 1));
                    dto.setStartNextBooking(startTimes.get(index));
                }
            }
            dtos.add(dto);
        }
        return dtos;
    }

    public Collection<ItemDto> findByString(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return repository.findAllByAvailableIsTrueAndNameContainingOrDescriptionContainingAllIgnoreCase(text, text)
                .stream().map(mapper::modelToDto).toList();
    }

    private void validateUser(Long userId) {
        if (userId == null) {
            throw new IdNotFoundException("User id is null!");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id" + userId + " not found!"));
    }

    private void validateItemOwner(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new IdMismatchException("Only the owner can update item information!");
        }
    }
}
