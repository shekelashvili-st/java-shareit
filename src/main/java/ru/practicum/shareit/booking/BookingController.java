package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestBody @Valid CreateBookingDto booking,
                             @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return service.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable @Positive Long bookingId,
                              @RequestParam boolean approved,
                              @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return service.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable @Positive Long bookingId,
                          @RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId) {
        return service.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllForBooker(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                            @RequestParam(defaultValue = "ALL") RequestBookingState state) {
        return service.getByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllForOwner(@RequestHeader(name = "X-Sharer-User-Id") @Positive Long userId,
                                           @RequestParam(defaultValue = "ALL") RequestBookingState state) {
        return service.getByOwnerId(userId, state);
    }
}
