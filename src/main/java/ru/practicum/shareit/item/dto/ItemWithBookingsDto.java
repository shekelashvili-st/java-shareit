package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Data
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;
}
