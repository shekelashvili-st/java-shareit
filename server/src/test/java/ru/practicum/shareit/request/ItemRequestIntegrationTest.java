package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.random.RandomGenerator;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {
    private static final RandomGenerator generator = RandomGenerator.getDefault();
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService service;

    @Test
    void testCreateRequest() {
        UserDto user1 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");

        ItemRequestDto request = service.create(newRequest, user1.getId());
        assertThat(request.getId(), notNullValue());
        assertThat(request.getRequester().getId(), equalTo(user1.getId()));
        assertThat(request.getDescription(), equalTo(newRequest.getDescription()));
    }

    @Test
    void testCreateRequestWrongId() {
        UserDto user1 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");

        assertThrows(IdNotFoundException.class, () -> service.create(newRequest, user1.getId() + 1));
    }

    @Test
    void testFindById() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");
        ItemRequestDto request = service.create(newRequest, user1.getId());
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(request.getId());
        ItemDto item = itemService.create(newItem, user2.getId());

        ItemRequestDto foundRequest = service.findById(request.getId());
        assertThat(foundRequest.getId(), equalTo(request.getId()));
        assertThat(foundRequest.getRequester().getId(), equalTo(user1.getId()));
        assertThat(foundRequest.getDescription(), equalTo(newRequest.getDescription()));
        assertThat(foundRequest.getItems().getFirst().getId(), equalTo(item.getId()));
    }

    @Test
    void testFindByIdWrongId() {
        UserDto user1 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");
        ItemRequestDto request = service.create(newRequest, user1.getId());

        assertThrows(IdNotFoundException.class, () -> service.findById(request.getId() + 2));
    }

    @Test
    void testFindAll() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");
        ItemRequestDto request = service.create(newRequest, user1.getId());

        ItemRequestDto foundRequest = service.findAll(user2.getId()).getFirst();
        assertThat(foundRequest.getId(), equalTo(request.getId()));
        assertThat(foundRequest.getRequester().getId(), equalTo(user1.getId()));
        assertThat(foundRequest.getDescription(), equalTo(newRequest.getDescription()));
    }

    @Test
    void testFindAllOwnRequest() {
        UserDto user1 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");
        ItemRequestDto request = service.create(newRequest, user1.getId());

        List<ItemRequestDto> foundRequest = service.findAll(user1.getId());
        assertThat(null, foundRequest.isEmpty());
    }

    @Test
    void testFindAllByRequesterId() {
        UserDto user1 = createUser();
        UserDto user2 = createUser();
        CreateItemRequestDto newRequest = new CreateItemRequestDto();
        newRequest.setDescription("desc");
        ItemRequestDto request = service.create(newRequest, user1.getId());
        ItemRequestDto request2 = service.create(newRequest, user2.getId());
        CreateItemDto newItem = new CreateItemDto();
        newItem.setName("name");
        newItem.setAvailable(true);
        newItem.setDescription("Desc");
        newItem.setRequestId(request.getId());
        ItemDto item = itemService.create(newItem, user2.getId());

        List<ItemRequestDto> foundRequests = service.findAllByRequesterId(user1.getId());
        ItemRequestDto foundRequest = foundRequests.getFirst();
        assertThat(null, foundRequests.size() == 1);
        assertThat(foundRequest.getId(), equalTo(request.getId()));
        assertThat(foundRequest.getRequester().getId(), equalTo(user1.getId()));
        assertThat(foundRequest.getDescription(), equalTo(newRequest.getDescription()));
        assertThat(foundRequest.getItems().getFirst().getId(), equalTo(item.getId()));
    }

    @Test
    void testFindAllByRequesterIdWrongId() {
        UserDto user1 = createUser();

        assertThrows(IdNotFoundException.class, () -> service.findAllByRequesterId(user1.getId() + 1));
    }

    private UserDto createUser() {
        CreateUserDto createUser = new CreateUserDto();
        createUser.setName("name" + generator.nextInt());
        createUser.setEmail("email" + generator.nextInt() + "@mail.ru");
        return userService.create(createUser);
    }
}

