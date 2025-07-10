package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Timestamp startPreviousBooking;
    private Timestamp startNextBooking;
}
