package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated // indicating that a specific class is supposed to be validated at the method level
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                         @RequestBody @Valid CreateItemDto item) {
        return client.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                         @RequestBody @Valid UpdateItemDto item,
                                         @PathVariable @Positive Long itemId) {
        return client.update(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable @Positive Long itemId) {
        return client.findById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return client.findAllForUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByString(@RequestParam String text) {
        return client.findByString(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                         @PathVariable @Positive Long itemId,
                                         @RequestBody @Valid CreateCommentDto comment) {
        return client.createComment(itemId, userId, comment);
    }
}
