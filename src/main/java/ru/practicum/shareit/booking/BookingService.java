package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NoItemsOwnedException;
import ru.practicum.shareit.exception.UserPermissionsException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper mapper;

    public BookingDto create(CreateBookingDto booking, Long bookerId) {
        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new IdNotFoundException("User with id=" + bookerId + " not found!"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(
                () -> new IdNotFoundException("Item with id=" + booking.getItemId() + " not found!"));
        if (!item.isAvailable()) {
            throw new ItemUnavailableException("Item with id=" + booking.getItemId() + " is unavailable!");
        }

        Booking createBooking = mapper.createDtoToModel(booking);
        createBooking.setBooker(booker);
        createBooking.setItem(item);
        Booking bookingInRepository = repository.save(createBooking);
        return mapper.modelToDto(bookingInRepository);
    }

    public BookingDto approve(Long bookingId, Long userId, boolean approved) {
        Booking booking = repository.findById(bookingId).orElseThrow(
                () -> new IdNotFoundException("Booking with id" + bookingId + " not found!"));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new UserPermissionsException("User with id=" + userId
                    + " does not own the item in booking with id=" + bookingId);
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return mapper.modelToDto(repository.save(booking));
    }

    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(
                () -> new IdNotFoundException("Booking with id" + bookingId + " not found!"));
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (Objects.equals(userId, ownerId) || Objects.equals(userId, bookerId)) {
            return mapper.modelToDto(booking);
        } else {
            throw new UserPermissionsException("User with id=" + userId
                    + " can't access booking with id=" + bookingId);
        }
    }

    public List<BookingDto> getByBookerId(Long bookerId, RequestBookingState requestBookingState) {
        Timestamp now = Timestamp.from(Instant.now());
        List<Booking> found = switch (requestBookingState) {
            case ALL -> repository.findAllByBookerIdOrderByStart(bookerId);
            case CURRENT -> repository.findAllByBookerIdAndStatusAndStartBeforeAndEndAfterOrderByStart(bookerId,
                    Status.APPROVED, now, now);
            case PAST -> repository.findAllByBookerIdAndStatusAndEndBeforeOrderByStart(bookerId,
                    Status.APPROVED, now);
            case FUTURE -> repository.findAllByBookerIdAndStatusAndStartAfterOrderByStart(bookerId,
                    Status.APPROVED, now);
            case WAITING -> repository.findAllByBookerIdAndStatusOrderByStart(bookerId, Status.WAITING);
            case REJECTED -> repository.findAllByBookerIdAndStatusOrderByStart(bookerId, Status.REJECTED);
        };
        return mapper.modelToDto(found);
    }

    public List<BookingDto> getByOwnerId(Long ownerId, RequestBookingState requestBookingState) {
        Timestamp now = Timestamp.from(Instant.now());
        if (!repository.existsByItemOwnerId(ownerId)) {
            throw new NoItemsOwnedException("User with id=" + ownerId + " does not own any items!");
        }
        List<Booking> found = switch (requestBookingState) {
            case ALL -> repository.findAllByItemOwnerIdOrderByStart(ownerId);
            case CURRENT -> repository.findAllByItemOwnerIdAndStatusAndStartBeforeAndEndAfterOrderByStart(ownerId,
                    Status.APPROVED, now, now);
            case PAST -> repository.findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStart(ownerId,
                    Status.APPROVED, now);
            case FUTURE -> repository.findAllByItemOwnerIdAndStatusAndStartAfterOrderByStart(ownerId,
                    Status.APPROVED, now);
            case WAITING -> repository.findAllByItemOwnerIdAndStatusOrderByStart(ownerId, Status.WAITING);
            case REJECTED -> repository.findAllByItemOwnerIdAndStatusOrderByStart(ownerId, Status.REJECTED);
        };
        return mapper.modelToDto(found);
    }
}
