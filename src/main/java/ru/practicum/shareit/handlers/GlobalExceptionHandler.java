package ru.practicum.shareit.handlers;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.IdMismatchException;
import ru.practicum.shareit.exception.IdNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IdNotFoundException.class, IdMismatchException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleIdNotFound(RuntimeException e) {
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, EmailAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(Throwable e) {
        return new ApiError(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleUncaught(Throwable e) {
        String message = e.getMessage();
        return new ApiError(message);
    }

    record ApiError(String description) {
    }
}
