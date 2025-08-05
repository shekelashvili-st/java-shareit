package ru.practicum.shareit.booking;

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
    public BookingDto create(@RequestBody CreateBookingDto booking,
                             @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return service.create(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId,
                              @RequestParam boolean approved,
                              @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return service.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Long bookingId,
                          @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return service.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllForBooker(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") RequestBookingState state) {
        return service.getByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllForOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") RequestBookingState state) {
        return service.getByOwnerId(userId, state);
    }
}
