package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.sql.Timestamp;

@Data
public class BookingDto {
    private Long id;

    private Item item;

    private User booker;

    private Timestamp start;
    private Timestamp end;

    private Status status;
}
