package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.IdMismatchException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.UserPermissionsException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
public class ItemServiceIntegrationTest {
    private static final RandomGenerator generator = RandomGenerator.getDefault();
    private final ItemService service;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService requestService;

    @Test
    void testSaveItemWithRequest() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");
        ItemRequestDto request = requestService.create(newRequest, user1.getId());
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(request.getId());

        ItemDto item = service.create(newItem, user2.getId());
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(newItem.getName()));
        assertThat(item.isAvailable(), equalTo(newItem.getAvailable()));
        assertThat(item.getDescription(), equalTo(newItem.getDescription()));
    }

    @Test
    void testSaveItemWrongRequest() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");
        ItemRequestDto request = requestService.create(newRequest, user1.getId());
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(request.getId() + 1);

        assertThrows(IdNotFoundException.class, () -> service.create(newItem, user2.getId()));
    }

    @Test
    void testSaveItemNoRequest() {
        UserDto user1 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);

        ItemDto item = service.create(newItem, user1.getId());
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(newItem.getName()));
        assertThat(item.isAvailable(), equalTo(newItem.getAvailable()));
        assertThat(item.getDescription(), equalTo(newItem.getDescription()));
    }

    @Test
    void testSaveItemWrongUserId() {
        UserDto user1 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);

        assertThrows(IdNotFoundException.class, () -> service.create(newItem, user1.getId() + 1));
    }

    @Test
    void testUpdateItem() {
        UserDto user1 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto item = service.create(newItem, user1.getId());
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("name2");
        updateItemDto.setAvailable(false);
        updateItemDto.setDescription("Desc2");

        ItemDto updatedItem = service.update(updateItemDto, item.getId(), user1.getId());
        assertThat(updatedItem.getId(), equalTo(item.getId()));
        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.isAvailable(), equalTo(updateItemDto.getAvailable()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
    }

    @Test
    void testUpdateItemWrongItemId() {
        UserDto user1 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto item = service.create(newItem, user1.getId());
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("name2");
        updateItemDto.setAvailable(false);
        updateItemDto.setDescription("Desc2");

        assertThrows(IdNotFoundException.class, () -> service.update(updateItemDto, item.getId() + 1, user1.getId()));
    }

    @Test
    void testUpdateItemWrongUserId() {
        UserDto user1 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto item = service.create(newItem, user1.getId());
        UpdateItemDto updateItemDto = new UpdateItemDto();
        updateItemDto.setName("name2");
        updateItemDto.setAvailable(false);
        updateItemDto.setDescription("Desc2");

        assertThrows(IdMismatchException.class, () -> service.update(updateItemDto, item.getId(), user1.getId() + 1));
    }

    @Test
    void testCreateComment() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());
        createBooking(user2.getId(), itemSaved.getId(), Instant.now(), Instant.now().plusMillis(10));
        Thread.sleep(20);
        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("comment");

        CommentDto savedComment = service.createComment(comment, itemSaved.getId(), user2.getId());
        assertThat(savedComment.getId(), notNullValue());
        assertThat(savedComment.getText(), equalTo(comment.getText()));
        assertThat(savedComment.getAuthorName(), equalTo(user2.getName()));
        assertThat(savedComment.getItem().getId(), equalTo(itemSaved.getId()));
    }

    @Test
    void testCreateCommentFutureBooking() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());
        createBooking(user2.getId(), itemSaved.getId(), Instant.now().plusSeconds(20), Instant.now().plusSeconds(30));
        Thread.sleep(20);
        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("comment");

        assertThrows(UserPermissionsException.class, () -> service.createComment(comment, itemSaved.getId(), user2.getId()));
    }

    @Test
    void testCreateCommentWrongUserId() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());
        createBooking(user2.getId(), itemSaved.getId(), Instant.now(), Instant.now().plusMillis(10));
        Thread.sleep(20);
        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("comment");

        assertThrows(IdNotFoundException.class, () -> service.createComment(comment, itemSaved.getId(), user2.getId() + 1));
    }

    @Test
    void testCreateCommentWrongItemId() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());
        createBooking(user2.getId(), itemSaved.getId(), Instant.now(), Instant.now().plusMillis(10));
        Thread.sleep(20);
        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("comment");

        assertThrows(IdNotFoundException.class, () -> service.createComment(comment, itemSaved.getId() + 1, user2.getId()));
    }

    @Test
    void testFindByIdWithComment() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());
        createBooking(user2.getId(), itemSaved.getId(), Instant.now(), Instant.now().plusMillis(10));
        createBooking(user2.getId(), itemSaved.getId(), Instant.now().plusSeconds(10),
                Instant.now().plusSeconds(10).plusMillis(10));
        Thread.sleep(20);

        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("comment");
        service.createComment(comment, itemSaved.getId(), user2.getId());

        ItemWithBookingsDto itemGet = service.findById(itemSaved.getId());
        assertThat(itemGet.getId(), equalTo(itemSaved.getId()));
        assertThat(itemGet.getName(), equalTo(itemSaved.getName()));
        assertThat(itemGet.isAvailable(), equalTo(itemSaved.isAvailable()));
        assertThat(itemGet.getDescription(), equalTo(itemSaved.getDescription()));
        assertThat(itemGet.getComments().getFirst().getText(), equalTo(comment.getText()));
    }

    @Test
    void testFindByIdWrongId() {
        UserDto user1 = createUser();
        createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());

        assertThrows(IdNotFoundException.class, () -> service.findById(itemSaved.getId() + 1));
    }

    @Test
    void testFindAllForUser() throws InterruptedException {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());
        BookingDto booking1 = createBooking(user2.getId(), itemSaved.getId(), Instant.now(), Instant.now().plusMillis(10));
        BookingDto booking2 = createBooking(user2.getId(), itemSaved.getId(), Instant.now().plusSeconds(10),
                Instant.now().plusSeconds(10).plusMillis(10));
        Thread.sleep(20);

        CreateCommentDto comment = new CreateCommentDto();
        comment.setText("comment");
        service.createComment(comment, itemSaved.getId(), user2.getId());

        ItemWithBookingsDto itemGet = service.findAllForUser(user1.getId()).stream().toList().getFirst();
        assertThat(itemGet.getId(), equalTo(itemSaved.getId()));
        assertThat(itemGet.getName(), equalTo(itemSaved.getName()));
        assertThat(itemGet.isAvailable(), equalTo(itemSaved.isAvailable()));
        assertThat(itemGet.getDescription(), equalTo(itemSaved.getDescription()));
        assertThat(itemGet.getComments().getFirst().getText(), equalTo(comment.getText()));
        assertThat(itemGet.getLastBooking().getId(), equalTo(booking1.getId()));
        assertThat(itemGet.getNextBooking().getId(), equalTo(booking2.getId()));
    }

    @Test
    void testFindByString() {
        UserDto user1 = createUser();
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(null);
        ItemDto itemSaved = service.create(newItem, user1.getId());

        ItemDto itemGet = service.findByString("Des").stream().toList().getFirst();
        assertThat(itemGet.getId(), equalTo(itemSaved.getId()));
        assertThat(itemGet.getName(), equalTo(itemSaved.getName()));
        assertThat(itemGet.isAvailable(), equalTo(itemSaved.isAvailable()));
        assertThat(itemGet.getDescription(), equalTo(itemSaved.getDescription()));
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
        BookingDto booking = bookingService.create(createBooking, userId);
        bookingService.approve(booking.getId(), booking.getItem().getOwner().getId(), true);
        return booking;
    }
}

