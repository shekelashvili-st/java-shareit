package ru.practicum.shareit.exception;

public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException(String msg) {
        super(msg);
    }
}
