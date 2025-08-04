package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateUserDto user) {
        return client.create(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @RequestBody UpdateUserDto user, @PathVariable @Positive long userId) {
        return client.update(userId, user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable @Positive long userId) {
        return client.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable @Positive long userId) {
        return client.deleteById(userId);
    }
}
