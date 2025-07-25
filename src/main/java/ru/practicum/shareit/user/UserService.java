package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    public UserDto create(CreateUserDto user) {
        validateEmail(user.getEmail());
        User userFromDb = repository.save(mapper.createDtoToModel(user));
        return mapper.modelToDto(userFromDb);
    }

    @Transactional
    public UserDto update(UpdateUserDto user, long userId) {
        User foundUser = repository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User with id=" + userId + " not found!"));
        String email = user.getEmail();
        if (email != null) {
            validateEmail(email);
            foundUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            foundUser.setName(user.getName());
        }
        User userFromDb = repository.save(foundUser);
        return mapper.modelToDto(userFromDb);
    }

    public UserDto getById(long id) {
        User foundUser = repository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("User with id=" + id + " not found!"));
        return mapper.modelToDto(foundUser);
    }

    @Transactional
    public void deleteById(long id) {
        repository.deleteById(id);
    }

    private void validateEmail(String email) {
        if (repository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("User with email=" + email + " already exists!");
        }
    }
}
