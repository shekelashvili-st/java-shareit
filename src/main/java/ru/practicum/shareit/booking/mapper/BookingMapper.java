package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    Booking createDtoToModel(CreateBookingDto createBookingDto);

    BookingDto modelToDto(Booking booking);

    List<BookingDto> modelToDto(List<Booking> booking);
}