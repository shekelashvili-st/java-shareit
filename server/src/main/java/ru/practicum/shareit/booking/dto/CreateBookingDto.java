package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.sql.Timestamp;

@Data
public class CreateBookingDto {

    @JsonIgnore
    private final Status status = Status.WAITING;
    @Positive
    @NotNull
    private Long itemId;
    @FutureOrPresent
    @NotNull
    private Timestamp start;
    @Future
    @NotNull
    private Timestamp end;

    @AssertTrue(message = "The end date must be after start date")
    private boolean isEndAfterStart() {
        return end.after(start);
    }
}
