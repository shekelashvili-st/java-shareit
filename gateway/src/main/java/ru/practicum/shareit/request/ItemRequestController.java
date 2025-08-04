package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                         @RequestBody @Valid CreateItemRequestDto itemRequest) {
        return client.create(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByRequesterId(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return client.findAllByRequesterId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return client.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable @Positive Long requestId) {
        return client.findById(requestId);
    }
}
