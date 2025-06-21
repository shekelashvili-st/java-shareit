package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated // indicating that a specific class is supposed to be validated at the method level
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto create(@Valid @RequestBody CreateUserDto user) {
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UpdateUserDto user, @PathVariable @Positive long userId) {
        return service.update(user, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable @Positive long userId) {
        return service.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable @Positive long userId) {
        service.deleteById(userId);
    }
}
