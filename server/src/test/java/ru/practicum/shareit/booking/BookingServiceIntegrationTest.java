package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.NoItemsOwnedException;
import ru.practicum.shareit.exception.UserPermissionsException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.random.RandomGenerator;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private static final RandomGenerator generator = RandomGenerator.getDefault();
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService service;

    @Test
    void testCreateBooking() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));

        BookingDto booking = service.create(newBooking, user2.getId());
        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getItem().getId(), equalTo(newBooking.getItemId()));
        assertThat(booking.getStart(), equalTo(newBooking.getStart()));
        assertThat(booking.getEnd(), equalTo(newBooking.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void testCreateBookingWrongUserId() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));

        assertThrows(IdNotFoundException.class, () -> service.create(newBooking, user2.getId() + 1));
    }

    @Test
    void testCreateBookingWrongItemId() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId() + 1);
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));

        assertThrows(IdNotFoundException.class, () -> service.create(newBooking, user2.getId()));
    }

    @Test
    void testCreateBookingUnavailable() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(false);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));

        assertThrows(ItemUnavailableException.class, () -> service.create(newBooking, user2.getId()));
    }

    @Test
    void testApprove() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        BookingDto approvedBooking = service.approve(booking.getId(), user1.getId(), true);
        assertThat(approvedBooking.getId(), equalTo(booking.getId()));
        assertThat(approvedBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(approvedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(approvedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(approvedBooking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void testReject() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        BookingDto approvedBooking = service.approve(booking.getId(), user1.getId(), false);
        assertThat(approvedBooking.getId(), equalTo(booking.getId()));
        assertThat(approvedBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(approvedBooking.getStart(), equalTo(booking.getStart()));
        assertThat(approvedBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(approvedBooking.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void testApproveWrongId() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        assertThrows(IdNotFoundException.class, () -> service.approve(booking.getId() + 1, user1.getId(), false));
    }

    @Test
    void testApproveNotOwner() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        assertThrows(UserPermissionsException.class, () -> service.approve(booking.getId(), user2.getId(), false));
    }

    @Test
    void testGetByIdOwner() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        BookingDto foundBooking = service.getById(booking.getId(), user1.getId());
        assertThat(foundBooking.getId(), equalTo(booking.getId()));
        assertThat(foundBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(foundBooking.getStart(), equalTo(booking.getStart()));
        assertThat(foundBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(foundBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void testGetByIdBooker() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        BookingDto foundBooking = service.getById(booking.getId(), user2.getId());
        assertThat(foundBooking.getId(), equalTo(booking.getId()));
        assertThat(foundBooking.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(foundBooking.getStart(), equalTo(booking.getStart()));
        assertThat(foundBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(foundBooking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void testGetByIdWrongId() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        assertThrows(IdNotFoundException.class, () -> service.getById(booking.getId() + 1, user1.getId()));
    }

    @Test
    void testGetByIdNotOwnerOrBooker() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        CreateBookingDto newBooking = new CreateBookingDto();
        newBooking.setItemId(item.getId());
        newBooking.setStart(Timestamp.from(Instant.now()));
        newBooking.setEnd(Timestamp.from(Instant.now().plusMillis(10)));
        BookingDto booking = service.create(newBooking, user2.getId());

        assertThrows(UserPermissionsException.class, () -> service.getById(booking.getId(), user2.getId() + 5));
    }


    @Test
    void testGetByBookerIdCurrent() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        createBooking(user2.getId(), item.getId(), Instant.now(), Instant.now().plusSeconds(10));
        Thread.sleep(10);

        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.ALL).size() == 1);
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.CURRENT).size() == 1);
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.FUTURE).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.PAST).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.WAITING).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.REJECTED).isEmpty());
    }

    @Test
    void testGetByBookerIdFuture() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        createBooking(user2.getId(), item.getId(), Instant.now().plusSeconds(10), Instant.now().plusSeconds(20));
        Thread.sleep(10);

        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.ALL).size() == 1);
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.CURRENT).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.FUTURE).size() == 1);
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.PAST).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.WAITING).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.REJECTED).isEmpty());
    }

    @Test
    void testGetByBookerIdPast() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        createBooking(user2.getId(), item.getId(), Instant.now(), Instant.now().plusMillis(5));
        Thread.sleep(10);

        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.ALL).size() == 1);
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.CURRENT).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.FUTURE).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.PAST).size() == 1);
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.WAITING).isEmpty());
        assertThat(null, service.getByBookerId(user2.getId(), RequestBookingState.REJECTED).isEmpty());
    }

    @Test
    void testGetByOwnerIdCurrent() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        createBooking(user2.getId(), item.getId(), Instant.now(), Instant.now().plusSeconds(10));
        Thread.sleep(10);

        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.ALL).size() == 1);
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.CURRENT).size() == 1);
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.FUTURE).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.PAST).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.WAITING).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.REJECTED).isEmpty());
    }

    @Test
    void testGetByOwnerIdFuture() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        createBooking(user2.getId(), item.getId(), Instant.now().plusSeconds(10), Instant.now().plusSeconds(20));
        Thread.sleep(10);

        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.ALL).size() == 1);
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.CURRENT).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.FUTURE).size() == 1);
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.PAST).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.WAITING).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.REJECTED).isEmpty());
    }

    @Test
    void testGetByOwnerIdPast() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        ItemDto item = itemService.create(newItem, user1.getId());
        createBooking(user2.getId(), item.getId(), Instant.now(), Instant.now().plusMillis(5));
        Thread.sleep(10);

        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.ALL).size() == 1);
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.CURRENT).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.FUTURE).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.PAST).size() == 1);
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.WAITING).isEmpty());
        assertThat(null, service.getByOwnerId(user1.getId(), RequestBookingState.REJECTED).isEmpty());
    }

    @Test
    void testGetByOwnerIdWrongId() {
        UserDto user1 = createUser();
        createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        itemService.create(newItem, user1.getId());

        assertThrows(NoItemsOwnedException.class, () -> service.getByOwnerId(user1.getId(), RequestBookingState.ALL));
    }


    private UserDto createUser() {
        CreateUserDto createUser = new CreateUserDto();
        createUser.setName("name" + generator.nextInt());
        createUser.setEmail("email" + generator.nextInt() + "@mail.ru");
        return userService.create(createUser);
    }

    private BookingDto createBooking(Long userId, Long itemId, Instant start, Instant end) {
        CreateBookingDto createBooking = new CreateBookingDto();
        createBooking.setItemId(itemId);
        createBooking.setStart(Timestamp.from(start));
        createBooking.setEnd(Timestamp.from(end));
        BookingDto booking = service.create(createBooking, userId);
        service.approve(booking.getId(), booking.getItem().getOwner().getId(), true);
        return booking;
    }
}

