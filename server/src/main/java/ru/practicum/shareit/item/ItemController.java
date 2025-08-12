package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @RequestBody CreateItemDto item) {
        return service.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                          @RequestBody UpdateItemDto item,
                          @PathVariable Long itemId) {
        return service.update(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto findById(@PathVariable Long itemId) {
        return service.findById(itemId);
    }

    @GetMapping
    public Collection<ItemWithBookingsDto> findAll(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return service.findAllForUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByString(@RequestParam String text) {
        return service.findByString(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @RequestBody CreateCommentDto comment) {
        return service.createComment(comment, itemId, userId);
    }
}
