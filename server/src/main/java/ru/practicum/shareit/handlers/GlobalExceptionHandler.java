package ru.practicum.shareit.handlers;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleBadRequest(EmailAlreadyExistsException e) {
        String message = e.getMessage();
        log.warn("Email already exists: {}", message);
        return new ApiError(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlePermissionException(UserPermissionsException e) {
        String message = e.getMessage();
        log.warn("User permission exception: {}", message);
        return new ApiError(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleUnavailable(ItemUnavailableException e) {
        String message = e.getMessage();
        log.warn("Item unavailable exception: {}", message);
        return new ApiError(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoItemsOwned(NoItemsOwnedException e) {
        String message = e.getMessage();
        log.warn("Item unavailable exception: {}", message);
        return new ApiError(message);
    }

    @ExceptionHandler({IdNotFoundException.class, IdMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleIdNotFound(RuntimeException e) {
        String message = e.getMessage();
        log.warn("Incorrect id: {}", message);
        return new ApiError(message);
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(Throwable e) {
        String message = e.getMessage();
        log.warn("Validation error occurred: {}", message);
        return new ApiError(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUncaught(Throwable e) {
        String message = e.getMessage();
        log.warn("Unexpected error occurred: {}", message);
        return new ApiError(message);
    }

    record ApiError(String description) {
    }
}
