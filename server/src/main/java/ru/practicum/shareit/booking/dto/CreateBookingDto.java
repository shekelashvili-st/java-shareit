package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.sql.Timestamp;

@Data
public class CreateBookingDto {

    @JsonIgnore
    private final Status status = Status.WAITING;

    private Long itemId;

    private Timestamp start;

    private Timestamp end;
}
