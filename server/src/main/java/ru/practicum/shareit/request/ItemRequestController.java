package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @RequestBody CreateItemRequestDto itemRequest) {
        return service.create(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByRequesterId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return service.findAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return service.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable Long requestId) {
        return service.findById(requestId);
    }
}
