package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto create(@Valid @RequestBody CreateUserDto user) {
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UpdateUserDto user, @PathVariable long userId) {
        return service.update(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return service.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        service.deleteById(userId);
    }
}
