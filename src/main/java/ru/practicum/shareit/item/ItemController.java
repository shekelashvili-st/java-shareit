package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated // indicating that a specific class is supposed to be validated at the method level
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                          @RequestBody @Valid CreateItemDto item) {
        return service.create(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                          @RequestBody @Valid UpdateItemDto item,
                          @PathVariable @Positive Long itemId) {
        return service.update(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable @Positive Long itemId) {
        return service.findById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return service.findAllForUser(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByString(@RequestParam String text) {
        return service.findByString(text);
    }
}
