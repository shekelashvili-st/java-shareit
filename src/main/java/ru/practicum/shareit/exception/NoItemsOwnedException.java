package ru.practicum.shareit.exception;

public class NoItemsOwnedException extends RuntimeException {
    public NoItemsOwnedException(String msg) {
        super(msg);
    }
}
