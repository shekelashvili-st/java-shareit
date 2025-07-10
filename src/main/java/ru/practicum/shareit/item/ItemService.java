package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.IdMismatchException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.UserPermissionsException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    @Transactional
    public ItemDto create(CreateItemDto item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id" + userId + " not found!"));
        Item newItem = mapper.createDtoToModel(item);
        newItem.setOwner(user);
        Item itemFromDb = repository.save(newItem);
        return mapper.modelToDto(itemFromDb);
    }

    @Transactional
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

    public ItemWithBookingsDto findById(Long itemId) {
        Item foundItem = repository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item with id " + itemId + " not found!"));
        ItemWithBookingsDto dto = mapper.modelToWithBookingsDto(foundItem);
        dto.setComments(commentRepository.findAllByItemId(itemId));
        return dto;
    }

    public Collection<ItemWithBookingsDto> findAllForUser(Long userId) {
        validateUser(userId);
        List<Item> items = repository.findAllByOwnerId(userId);
        List<ItemWithBookingsDto> dtos = new ArrayList<>();
        for (Item item : items) {
            ItemWithBookingsDto dto = mapper.modelToWithBookingsDto(item);
            List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStart(dto.getId());
            List<Timestamp> startTimes = bookings
                    .stream().map(Booking::getStart).toList();
            if (!startTimes.isEmpty()) {
                int index = Collections.binarySearch(startTimes, Timestamp.from(Instant.now()));
                if (index < 0) {
                    index = -index - 1;
                }
                if (index == 0) {
                    dto.setNextBooking(bookings.get(index));
                } else if (index == startTimes.size()) {
                    dto.setLastBooking(bookings.get(index - 1));
                } else {
                    dto.setLastBooking(bookings.get(index - 1));
                    dto.setNextBooking(bookings.get(index));
                }
            }
            dto.setComments(commentRepository.findAllByItemId(item.getId()));
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public CommentDto createComment(CreateCommentDto comment, Long itemId, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id" + userId + " not found!"));
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item with id " + itemId + " not found!"));
        validateItemBooker(itemId, userId);
        Comment newComment = commentMapper.createDtoToModel(comment);
        newComment.setAuthor(author);
        newComment.setItem(item);

        Comment commentInRepository = commentRepository.save(newComment);
        CommentDto dto = commentMapper.modelToDto(commentInRepository);
        dto.setAuthorName(author.getName());
        dto.setItem(item);
        return dto;
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

    private void validateItemBooker(Long itemId, Long userId) {
        boolean exists = bookingRepository.existsByItemIdAndBookerIdAndStatusAndStartBefore(itemId, userId, Status.APPROVED, Timestamp.from(Instant.now()));
        if (!exists) {
            throw new UserPermissionsException("Only users who rented the item can leave comments!");
        }
    }
}
