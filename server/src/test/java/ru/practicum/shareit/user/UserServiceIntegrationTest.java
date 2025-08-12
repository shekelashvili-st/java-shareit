package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserService service;

    @Test
    void testSaveUser() {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setName("name");
        newUser.setEmail("test@test.com");

        UserDto user = service.create(newUser);
        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUser.getName()));
        assertThat(user.getEmail(), equalTo(newUser.getEmail()));
    }

    @Test
    void testSaveEmailAlreadyExists() {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setName("name");
        newUser.setEmail("test@test.com");
        CreateUserDto newUser2 = new CreateUserDto();
        newUser2.setName("name2");
        newUser2.setEmail("test@test.com");


        service.create(newUser);
        assertThrows(EmailAlreadyExistsException.class, () -> service.create(newUser2));
    }

    @Test
    void testUpdateUser() {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setName("name");
        newUser.setEmail("test@test.com");
        UpdateUserDto updatedUser = new UpdateUserDto();
        updatedUser.setName("name2");
        updatedUser.setEmail("test2@test.com");
        UserDto user = service.create(newUser);

        UserDto updatedUserInDb = service.update(updatedUser, user.getId());
        assertThat(updatedUserInDb.getId(), equalTo(user.getId()));
        assertThat(updatedUserInDb.getName(), equalTo(updatedUser.getName()));
        assertThat(updatedUserInDb.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    void testUpdateUserWrongId() {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setName("name");
        newUser.setEmail("test@test.com");
        UpdateUserDto updatedUser = new UpdateUserDto();
        updatedUser.setName("name2");
        updatedUser.setEmail("test2@test.com");
        UserDto user = service.create(newUser);

        assertThrows(IdNotFoundException.class, () -> service.update(updatedUser, user.getId() + 1));
    }

    @Test
    void testGetById() {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setName("name");
        newUser.setEmail("test@test.com");
        UserDto user = service.create(newUser);

        UserDto foundUser = service.getById(user.getId());
        assertThat(foundUser.getId(), equalTo(user.getId()));
        assertThat(foundUser.getEmail(), equalTo(user.getEmail()));
        assertThat(foundUser.getName(), equalTo(user.getName()));
    }

    @Test
    void testGetByIdWrongId() {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setName("name");
        newUser.setEmail("test@test.com");
        UserDto user = service.create(newUser);

        assertThrows(IdNotFoundException.class, () -> service.getById(user.getId() + 1));
    }

    @Test
    void testDelete() {
        CreateUserDto newUser = new CreateUserDto();
        newUser.setName("name");
        newUser.setEmail("test@test.com");
        UserDto user = service.create(newUser);

        UserDto foundUser = service.getById(user.getId());
        service.deleteById(user.getId());

        assertThat(foundUser.getId(), equalTo(user.getId()));
        assertThat(foundUser.getEmail(), equalTo(user.getEmail()));
        assertThat(foundUser.getName(), equalTo(user.getName()));
        assertThrows(IdNotFoundException.class, () -> service.getById(user.getId()));
    }
}

